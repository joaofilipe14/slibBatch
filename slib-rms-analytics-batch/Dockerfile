FROM eclipse-temurin:17.0.11_9-jre-ubi9-minimal
RUN useradd xld  --home /home/xld/ --create-home --groups users --shell /bin/bash

RUN mkdir -p /home/data/var/log/slib-rms-analytics-batch /home/data/slib-rms-analytics-batch/bin /home/data/slib-rms-analytics-batch/lib  && chown xld -R /home/data
USER xld

COPY --chown=xld target/slib-rms-analytics-batch-*-exec.jar /home/data/slib-rms-analytics-batch/lib/
COPY --chown=xld src/main/setup-scripts/slib-rms-analytics-batch.sh /home/data/slib-rms-analytics-batch/bin/

RUN ["chmod", "+x", "/home/data/slib-rms-analytics-batch/bin/slib-rms-analytics-batch.sh"]
ENTRYPOINT ["sh", "/home/data/slib-rms-analytics-batch/bin/slib-rms-analytics-batch.sh"]