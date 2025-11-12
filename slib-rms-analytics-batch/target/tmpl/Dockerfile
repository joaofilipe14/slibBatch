FROM ${jdk.docker.image}

RUN useradd xld  --home /home/xld/ --create-home --groups users --shell /bin/bash
RUN mkdir -p /home/data/var/log /home/data/app/bin /home/data/app/lib  && chown xld:users -R /home/data
USER xld:users
COPY --chown=xld *-exec.jar /home/data/app/lib/
COPY --chown=xld docker-app.sh /home/data/app/bin/
RUN ["chmod", "+x", "/home/data/app/bin/docker-app.sh"]
ENTRYPOINT ["sh", "/home/data/app/bin/docker-app.sh"]

