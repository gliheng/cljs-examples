# notebook

## Overview

This is a Clojurescript/reagent/figwheel demo. It shows how to do the following with clojurescript

- How to write reagent component
- How to composite components
- How to write reloadable code using defonce
- How to make backend request with core.async
- How to feed data from backend to UI

## TODO

Better architecture using reframe.

## Setup

To get an interactive development environment run:

    rlwrap lein figwheel

and open your browser at [localhost:3449](http://localhost:3449/).

To do production build, run:

    lein do clean, cljsbuild once min

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
