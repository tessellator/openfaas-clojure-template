FROM openjdk:8u121-jdk-alpine as builder

RUN apk --no-cache add curl bash \
    && addgroup -S app && adduser -S -g app app \
    && echo "Downloading Clojure installer." \
    && curl -O https://download.clojure.org/install/linux-install-1.10.0.411.sh \
    && chmod +x linux-install-1.10.0.411.sh \
    && ./linux-install-1.10.0.411.sh \
    && apk del curl

WORKDIR /home/app/entrypoint
COPY . /home/app

RUN clojure -A:depstar -m hf.depstar.uberjar Entrypoint.jar

## -----------------------------------------------------------------------------

FROM openjdk:8u121-jdk-alpine as ship
RUN apk --no-cache add curl bash \
    && addgroup -S app && adduser -S -g app app \
    && echo "Pulling watchdog binary from Github." \
    && curl -sSL https://github.com/openfaas-incubator/of-watchdog/releases/download/0.4.6/of-watchdog > /usr/bin/fwatchdog \
    && chmod +x /usr/bin/fwatchdog \
    && apk del curl

WORKDIR /home/app
COPY --from=builder /home/app/entrypoint/Entrypoint.jar ./Entrypoint.jar

RUN chown -R app /home/app
USER app

ENV upstream_url="http://127.0.0.1:4000"
ENV mode="http"

ENV fprocess="java -cp Entrypoint.jar clojure.main -m entrypoint.core"
EXPOSE 8080

HEALTHCHECK --interval=2s CMD [ -e /tmp/.lock ] || exit 1

CMD ["fwatchdog"]
