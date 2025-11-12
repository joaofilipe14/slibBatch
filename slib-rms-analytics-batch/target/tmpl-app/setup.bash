#!/bin/bash

# to enable debug mode
# export log_level=DEBUG

logger() {
  local ltime
  printf -v ltime '%(%F:%T)T'
  echo "$ltime $*"
}

error() {
  logger ERROR "$@" >&2
  exit 1
}

info() {
  logger INFO "$@"
}

debug() {
  [[ $log_level = DEBUG ]] && logger DEBUG "$@"
}

check_simple_cmd() {
  debug "+ $@"
  "$@" || error "failed to $*"
}

set_java() {
  export JAVA_HOME=$app_home/jdk
  java=$JAVA_HOME/bin/java
  export SPRING_CONFIG_LOCATION=$app_home/conf/
  export JASYPT_ENCRYPTOR_PASSWORD=$(<$jasypt_file)
  local java_options="{{spr.java.options}}"
  [[ $java_options ]] && export _JAVA_OPTIONS=$java_options
}

app_home=/home/data{{shared.env.code}}/slib-rms-analytics-batch
jdk_download_url={{jdk.download.url}}
jdk_tgz=${jdk_download_url##*/}
jdk_dir=$HOME/jdk/${jdk_tgz%.tar.gz}
jdk_nblinks=$jdk_dir/nblinks
jasypt_file=conf/.jasypt.conf
pid_file=application.pid
log_file=/home/data{{shared.env.code}}/var/log/slib-rms-analytics-batch/slib-rms-analytics-batch.log

check_deployment() {
  check_simple_cmd test "${USER#xld_}" = "{{shared.env.code}}"
  info check $app_home/*-archive.zip exists
  archive_zip=(*-archive.zip)
  check_simple_cmd test -e $archive_zip
  info check $app_home/conf/ does not exist
  check_simple_cmd test ! -e conf/
}

extract_jdk_once() {
  [[ -d $HOME/jdk ]] || mkdir $HOME/jdk
  [[ -f $jdk_nblinks ]] && return
  info extract jdk
  check_simple_cmd mkdir $jdk_dir
  check_simple_cmd wget --no-proxy "$jdk_download_url" -P $HOME/jdk
  check_simple_cmd tar zxvf $HOME/jdk/$jdk_tgz -C $jdk_dir
  check_simple_cmd rm $HOME/jdk/$jdk_tgz
  check_simple_cmd touch $jdk_nblinks
}

link_jdk() {
  check_simple_cmd ln -s $jdk_dir jdk
  check_simple_cmd ln $jdk_nblinks jdk_nblinks
}

remove_jdk_if_no_links() {
  [[ $(stat -c %h $jdk_nblinks) = 1 ]] && rm -rf $jdk_dir
}

encrypt_passwords() {
  check_simple_cmd unzip -d lib/tmp lib/*.jar 'BOOT-INF/lib/jasypt-*.jar' -x '**jasypt-spring*'
  local cmd="$java -cp lib/tmp/BOOT-INF/lib/jasypt-*.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI "
  cmd+='algorithm=PBEWITHHMACSHA512ANDAES_256 ivGeneratorClassName=org.jasypt.iv.RandomIvGenerator '
  cmd+="password='$JASYPT_ENCRYPTOR_PASSWORD' input='%s'"
  local perlol='$q=$ARGV=~/yml$/?q("):"";'
  perlol+='s/passwords?(?!.*bcrypt)\s*+[:=]\s*+\K"?(.+?)"?$/$cmd=sprintf $ENV{cmd},$1=~s-\047-\047\134\047\047-gr;@a=qx($cmd)=~m(.+)g;qq(${q}ENC($a[$#a])${q})/ge'
  cmd=$cmd perl -i -pe "$perlol" conf/*.*
}

setup_install() {
  check_deployment
  extract_jdk_once
  link_jdk
  info extract archive
  check_simple_cmd unzip -d . $archive_zip
  check_simple_cmd rm $archive_zip
  setup_bash=(*-setup.bash)
  [[ -e $setup_bash ]] && ln -f "$setup_bash" setup.bash
  check_simple_cmd chmod +x jdk/bin/* setup.bash
  check_simple_cmd chmod 600 "$jasypt_file"
  set_java
  encrypt_passwords
  info application start
  check_simple_cmd app_start
}

setup_uninstall() {
  info stopping application
  check_simple_cmd app_stop
  rm -rf "${app_home:?}"/{conf,bin,lib,jdk*}
  remove_jdk_if_no_links
  true
}

app_is_running() {
  kill -0 "$java_app_pid" 2>/dev/null
}

app_status() {
  status=stopped
  [[ -f $pid_file ]] && java_app_pid=$(<$pid_file) && app_is_running && status=started
}

start_sleep_s=5
start_timeout=50
stop_sleep_s=2
stop_timeout=20

start_tail_logs() {
  tail -f "$log_file" & tail_pid=$!
}

start_java_app() {
  set_java
  local jar=( lib/*.jar )
  ( "$java" -Dlogging.config=conf/logback-spring.xml -jar "$jar" >& /dev/null & )
}

kill_tail_logs() {
  kill -15 $tail_pid 2>/dev/null
}

kill_java_app() {
  kill -15 $java_app_pid 2>/dev/null
}

app_start() {
  app_status
  [[ $status = started ]] && { info "already running, nothing to do"; return;}
  local s
  printf -v s '%(%s)T'
  [[ -e $log_file ]] && { mv ${log_file%.log}{.log,-$s.log} || rm "$log_file";}
  start_java_app
  SECONDS=0
  until sleep $start_sleep_s; grep -q "Started.*Application" "$log_file" && status=started; do
    [[ -z $tail_pid ]] && start_tail_logs
    info "starting... ($SECONDS s)"
    if ((SECONDS>=start_timeout)) || grep -q Error.starting.ApplicationContext "$log_file"; then
      break
    fi
  done
  kill_tail_logs
  [[ $status = started ]] || error "failed to start"
}

app_stop() {
  app_status
  [[ $status = stopped ]] && { info "not running, nothing to do"; return;}
  kill_java_app
  SECONDS=0
  until sleep $stop_sleep_s; ! app_is_running && status=stopped; do
    info "stopping... ($SECONDS s)"
    if ((SECONDS>=stop_timeout)); then
      error "failed to stop"
    fi
  done
}

check_simple_cmd cd $app_home
if { [[ $1 = *start ]] || [[ $1 = install ]];} && [ -e *-archive.zip ]; then
  set reinstall
fi
case "$1" in
install)
  setup_install
  ;;
uninstall)
  setup_uninstall
  ;;
reinstall)
  setup_uninstall
  setup_install
  ;;
start)
  app_start
  ;;
stop)
  app_stop
  ;;
restart)
  app_stop
  app_start
  ;;
status)
  app_status
  info status = $status
  [[ $status = started ]] && ps -p "$java_app_pid" -f
  ;;
esac
