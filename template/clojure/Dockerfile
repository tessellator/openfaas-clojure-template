FROM clojure:openjdk-17-tools-deps as builder

WORKDIR /home/app/entrypoint

COPY ./entrypoint/deps.edn ./deps.edn
COPY ./function/deps.edn ./function/manifest* ../function/

RUN clojure -P
RUN clojure -A:build -P

WORKDIR /home/app/function

RUN clojure -P
COPY ./function/src ./src

WORKDIR /home/app/entrypoint
COPY ./entrypoint/src ./src
COPY ./entrypoint/classes ./classes
COPY ./entrypoint/build.clj ./build.clj

RUN clojure -T:build uber
RUN if [ -f ../function/manifest.mf ] ; then echo "applying manifest" && jar umf ../function/manifest.mf ./bin/Entrypoint.jar ; fi

FROM openjdk:17-slim-bullseye as ship

RUN groupadd app && useradd -g app app
USER app:app

ENV upstream_url="http://127.0.0.1:4000"
ENV mode="http"

ENV fprocess="java -jar Entrypoint.jar"
EXPOSE 8080

HEALTHCHECK --interval=2s CMD [ -e /tmp/.lock ] || exit 1

# Do not let the watchdog create the lock file. The watchdog can create the lock
# file before the Jetty instance is actually ready, causing the function to be
# exposed too early and causing 500 Internal Server Error responses. Instead,
# rely on the entrypoint to create the lock file when Jetty is ready.
ENV suppress_lock=true

CMD ["./fwatchdog"]

COPY --chown=app --from=ghcr.io/openfaas/of-watchdog:0.8.4 /fwatchdog ./fwatchdog
COPY --chown=app --from=builder /home/app/entrypoint/bin/Entrypoint.jar ./Entrypoint.jar
