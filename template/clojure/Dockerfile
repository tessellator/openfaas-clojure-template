FROM tessellator/openfaas-clojure-builder:latest as builder

WORKDIR /home/app/entrypoint
COPY . /home/app

RUN clojure -e "(compile 'entrypoint.core)" \
    && clojure -A:depstar -m hf.depstar.uberjar Entrypoint.jar \
    && if [ -f ../function/manifest.mf ] ; then echo "applying manifest" && jar umf ../function/manifest.mf ./Entrypoint.jar ; fi

## -----------------------------------------------------------------------------

FROM openjdk:8u181-jdk-alpine as ship
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

ENV fprocess="java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -cp Entrypoint.jar clojure.main -m entrypoint.core"
EXPOSE 8080

HEALTHCHECK --interval=2s CMD [ -e /tmp/.lock ] || exit 1

# Do not let the watchdog create the lock file. The watchdog can create the lock
# file before the Jetty instance is actually ready, causing the function to be
# exposed too early and causing 500 Internal Server Error responses. Instead,
# rely on the entrypoint to create the lock file when Jetty is ready.
ENV suppress_lock=true

CMD ["fwatchdog"]
