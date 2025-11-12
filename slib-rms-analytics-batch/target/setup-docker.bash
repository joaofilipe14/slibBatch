#!/bin/bash

app() {

  ### constants ###
  env_code='{{shared.env.code}}'
  app_id='<<xld-tile-app-id>>'
  deploy_home=/home/data$env_code/$app_id
  config_dir=$app_id-docker-config-$env_code
  docker_compose=(docker compose -f "$config_dir/docker-compose.yml")

  ### logging functions ###
  # debug enabled by default, to disable : export debug=0
  debug=${debug:-1}

  interrupt() {
    local sid=$(ps -p$$ -osid=)
    kill -INT $$
    pkill -s $sid
  }

  trap '((${#pids[@]}>0)) && { kill ${pids[@]}; wait ${pids[@]} 2>/dev/null;}' exit

  if perl -MTime::HiRes=time -MPOSIX=strftime -e 0; then
    add_time_ms=( perl -MTime::HiRes=time -MPOSIX=strftime -ne 'BEGIN{$prefix=shift@ARGV}
    $t=time;printf"%s.%03d %s %s",(strftime"%F %T",localtime$t),($t-int$t)*1000,$prefix,$_' )
    mkfifo err.f inf.f deb.f
    exec 3<>err.f 4<>inf.f 5<>deb.f
    rm err.f inf.f deb.f
    "${add_time_ms[@]}" ERROR >&2 <&3 & pids+=($!); error() { echo "$@" >&3; interrupt; }
    "${add_time_ms[@]}" INFO <&4 & pids+=($!); info() { echo "$@" >&4; }
    if [[ $debug = 1 ]]; then
      "${add_time_ms[@]}" DEBUG >&1 <&5 & pids+=($!); debug() { echo "$@" >&5;}
    else
      debug() { :;}
    fi
  else
    logger() { local ltime; printf -v ltime '%(%F:%T)T'; echo "$ltime $*";}
    error() { logger ERROR "$@" >&2; interrupt;}
    info() { logger INFO "$@";}
    if [[ $debug = 1 ]]; then
      debug() { logger DEBUG "$@";}
    else
      debug() { :;}
    fi
  fi

  status_docker() {
    "${docker_compose[@]}" ps
  }

  start_docker() {
    info "=========================DOCKER========================="
    "${docker_compose[@]}" config
    "${docker_compose[@]}" up -d --wait
    status_docker
    info "=========================DOCKER========================="
  }

  stop_docker() {
    info "=========================DOCKER========================="
    "${docker_compose[@]}" down --remove-orphans --rmi all
    info "=========================DOCKER========================="
  }

  case $1 in
  status) status_docker ;;
  start) start_docker ;;
  stop)  stop_docker ;;
  esac
}

# to set variables and functions definition from app
app

### control functions ###
check_simple_cmd() { debug "+ $*"; "$@" || error "failed to $*" || exit 1;}
check_file_exists() { [[ -e $1 ]] || error "$1 does not exist";}
check_file_doesnt_exist() { [[ ! -e $1 ]] || error "$1 exists";}
check_string_equals() { [[ $1 = $2 ]] || error "$1 != $2";}
check_starts_with() { [[ $1 = $2* ]] || error "$1 does not start with $2";}

### encrypt functions ###
jasypt_file=$config_dir/.docker-jasypt.conf
mv_jasypt_file() {
  for dir in "$config_dir"/*/; {
    ln "$jasypt_file" "$dir"
  }
  rm "$jasypt_file"
}

encrypt_passwords() {
  jasypt_service_urls='{{jasypt.service.url}}'
  check_simple_cmd . "$jasypt_file"
  # failover
  for jasypt_service_url in $jasypt_service_urls; {
    check_starts_with "$jasypt_service_url" 'https://'
    [[ $(curl -s -G -w '%{http_code}' ${jasypt_service_url%jasypt}/content/index.html) = *200 ]] && break;
  } || error "no crypt service available"
  export cmd="curl -s -G -w '%{http_code}' '$jasypt_service_url' --data-urlencode $'jasyptkey=%s' --data-urlencode $'input=%s'"
  local perlol='$q=$ARGV=~/ya?ml$/?q("):"";s(passwords?\s*+[:=]\s*+\K"?(?!\{\w+\})(.+?)"?$)'
  perlol+='{$cmd=sprintf $ENV{cmd},$ENV{JASYPT_ENCRYPTOR_PASSWORD},$1=~s-(?<!\\)[\047]-\\$&-gr;'
  perlol+='qx($cmd)=~m(<pre>(.+)</pre>200) or die;qq(${q}ENC($1)${q})}ge'
  check_simple_cmd perl -i -pe "$perlol" "$config_dir"/*/*.*
  check_simple_cmd mv_jasypt_file
  # use default spring boot server.port 8080 because of docker port mapping
  check_simple_cmd perl -i -ne 'print if ! /^server.port=/' "$config_dir"/*/root.properties
}

### setup functions ###
check_deployment() {
  check_string_equals "${USER#xld_}" "$env_code"
  check_file_exists "$archive_zip"
  check_file_doesnt_exist "$deploy_home/docker-config"
}

extract_archive() {
  check_simple_cmd test -n "${config_dir}"
  check_simple_cmd rm -rf "${config_dir}/"
  check_simple_cmd unzip -d . "$archive_zip"
  check_simple_cmd rm "$archive_zip"
  check_simple_cmd mv docker-config $app_id-docker-config-$env_code
  check_simple_cmd perl -i -pe "s(/docker-config/)(/$app_id-docker-config-$env_code/)" "$config_dir"/*/docker-compose-service.yml
  check_simple_cmd chmod 600 "$jasypt_file"
  check_simple_cmd encrypt_passwords
}

concat_config() {
  check_simple_cmd cat "$config_dir"/{docker-compose-header,*/docker-compose-service,docker-compose-footer}.yml > "$config_dir/docker-compose.yml"
  rm "$config_dir"/{docker-compose-header,*/docker-compose-service,docker-compose-footer}.yml
}

# with current user (otherwise will owned by root)
create_dir_if_necessary() {
  for dir in $(grep -Po '/home/data[^/][^:"]*' "$config_dir/docker-compose.yml"); {
    [[ -d $dir ]] || check_simple_cmd mkdir "$dir"
  }
}

export_and_append_if_missing() {
  typeset -F "$1" >/dev/null || return 1
  "$1"
  grep -qF "$1" $HOME/.bashrc >/dev/null && return
  {
    typeset -f "$1"
    echo "$1"
  } >> $HOME/.bashrc
}

export_and_setup_bashrc() {
  export_current_uid() {
    export CURRENT_UID=$(id -u):$(id -g)
  }
  export_and_append_if_missing export_current_uid
  export_tz() {
    export TZ=$(readlink /etc/localtime|grep -Po '\w+/\w+$')
  }
  export_and_append_if_missing export_tz
}

create_app_script() {
  { echo "#!/bin/bash"; typeset -f app; echo 'app "$1"';} > "$deploy_home"/app.bash
  chmod +x "$deploy_home"/app.bash
}

setup_install() {
  check_simple_cmd check_deployment
  check_simple_cmd extract_archive
  check_simple_cmd concat_config
  check_simple_cmd create_dir_if_necessary
  check_simple_cmd export_and_setup_bashrc
  check_simple_cmd create_app_script
  check_simple_cmd start_docker
}

setup_uninstall() {
  check_simple_cmd stop_docker
  # directories will be removed at destroy because targetPathShared = false
}

setup_uninstall_if_needed() {
  [[ ! -e $config_dir ]] || setup_uninstall
}

main() {
  [[ $1 ]] && {
    app $1
    return
  }
  check_simple_cmd cd "$deploy_home"
  archive_zip=(*-archive.zip)
  if [[ ${#archive_zip[@]} = 1 && -e $archive_zip ]]; then
    setup_uninstall_if_needed
    check_simple_cmd setup_install
  else
    error should never occur
  fi
}

main
