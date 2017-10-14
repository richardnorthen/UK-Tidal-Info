# UK Tidal Info

[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg?style=flat)](https://github.com/RichardLitt/standard-readme)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

> Collects and displays tidal information from around the UK.

A command-line interface program written in Java that collects and displays tidal information from monitoring stations around the United Kingdom. It utilizes the Environment Agency's [Tide Gauge API](https://environment.data.gov.uk/flood-monitoring/doc/tidegauge#measures) (in beta) to list tidal information for various stations around the UK, and Google's [Gson](https://github.com/google/gson) for deserializing JSON into Java Objects.

## Install

1. `build`

## Usage

```
Usage:
  program list [search <id | name | area>]
  program station <id>
```

## Maintainer

[Richard Northen](https://github.com/richardnorthen)

## Contribute

Not sure why you'd want to contribute to such a trivial program, but sure, I'll accept PRs that look reasonable.

## License

Uses the [MIT License © 2017 Richard Northen](LICENSE)