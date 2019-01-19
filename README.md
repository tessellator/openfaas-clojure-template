# OpenFaaS Clojure Template


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


## License

Copyright © 2019 Thomas C. Taylor and contributors.

Distributed under the MIT License, the same as OpenFaaS.
