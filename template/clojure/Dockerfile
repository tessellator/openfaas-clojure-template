FROM openjdk:8u121-jdk-alpine as builder
# NOTE: If you are targeting ARM64, you will run into an error because the
# image above does not yet support that architecture. See this issue for more
# details: https://github.com/tessellator/openfaas-clojure-template/issues/6. 
# To work around this, comment out the FROM line above and uncomment the one 
# below.
#FROM balenalib/generic-aarch64-alpine-openjdk:8-20210225 as builder

ENV CLOJURE_VERSION="1.10.3.822"

RUN apk --no-cache add curl bash \
    && curl -O https://download.clojure.org/install/linux-install-${CLOJURE_VERSION}.sh \
    && chmod +x linux-install-${CLOJURE_VERSION}.sh \
    && ./linux-install-${CLOJURE_VERSION}.sh

WORKDIR /home/app/entrypoint
COPY ./entrypoint/deps.edn ./deps.edn
COPY ./function/deps.edn ../function/deps.edn

RUN clojure -A:uberjar -Stree

# During the build step, the dev dependencies from the function are fetched. I
# do not know why. This line will go and fetch those dependencies as an
# independent step so that if only the function code changes there will be no
# need to fetch the dev dependencies.
RUN cd ../function && clojure -A:dev -Stree && cd ../entrypoint

COPY . /home/app

RUN clojure -e "(compile 'entrypoint.core)" \
    && clojure -A:depstar -m hf.depstar.uberjar Entrypoint.jar \
    && if [ -f ../function/manifest.mf ] ; then echo "applying manifest" && jar umf ../function/manifest.mf ./Entrypoint.jar ; fi

## -----------------------------------------------------------------------------

FROM alpine:latest as fwatchdog-fetcher

WORKDIR /home

ENV WATCHDOG_VERSION="0.8.3"

RUN apk add curl \
    && curl -sSL https://github.com/openfaas/of-watchdog/releases/download/${WATCHDOG_VERSION}/fwatchdog-amd64 > fwatchdog \
    && chmod +x ./fwatchdog

## -----------------------------------------------------------------------------

FROM openjdk:8u181-jdk-alpine as ship

RUN addgroup -S app && adduser -S app -G app
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

CMD ["./fwatchdog"]

COPY --chown=app --from=fwatchdog-fetcher /home/fwatchdog ./fwatchdog
COPY --chown=app --from=builder /home/app/entrypoint/Entrypoint.jar ./Entrypoint.jar