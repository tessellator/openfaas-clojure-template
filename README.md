# OpenFaaS Clojure Template

For a more in-depth walkthrough, please see my [introductory post](http://www.tessellator.net/2019-01-25-introducing-openfaas-clojure-template/).


## Installation

To install this template, run the following command:

```
faas-cli template pull https://github.com/tessellator/openfaas-clojure-template
```

If you ever need to update the template, simply run the the command above with
the `--overwrite` flag.

This command will create (if necessary) a new folder `template` and add this
template to it.

You can read more about how templates work in the
[faas-cli documentation](https://github.com/openfaas/faas-cli/blob/master/guide/TEMPLATE.md).


## Usage

Create a new function with the following command:

```
faas-cli new my-function --lang=clojure
```

A new project using `deps.edn` will be created. It will contain a `function.core`
namespace that is required for the template to work properly. The requirement
for this namespace is to have a ring handler defined as `app`. The app will
be run in a jetty server in production.

You may add and use ring middleware and other libraries per usual.


### Using compojure

If you would like your function to support subroutes of your main function
route, you can use compojure to specify the subroutes. It is important to note
that you only need to specify the subroute in your route definitions.

For example, in order to handle a call to `/function/my-function/subroute/1234`,
the `my-function` function might contain the following definition:

```clojure
(GET "/subroute/:id" [id]
  ...)
```


### Adding items to the uberjar manifest

It can occasionally be useful to add properties to the uberjar, such as
specifying the `Implementation-Version`. This template will apply the properties
defined in a `manifest.mf` file in the top-level function folder (alongside the
`deps.edn` file).


### Running a server during local development

The template provides development tools for running a Jetty server locally
during development. The additional features are associated with the `dev` alias,
so to access them you must use the following command to launch a REPL.

```
clj -A:dev
```

The REPL will now have access to the `function.server` namespace located in the
dev folder. You can start the server with the following commands:

```clojure
(require '[function.server :as server])
(server/start!)
```

You can provide [Jetty options](http://ring-clojure.github.io/ring/ring.adapter.jetty.html#var-run-jetty)
to the `start!` function. For convenience, the options are stored between calls
and are not necessary for subsequent calls to `start!`.

You can stop the server by calling the `stop!` function.


## License

Copyright © 2019 Thomas C. Taylor and contributors.

Distributed under the MIT License, the same as OpenFaaS.
