# UK Tidal Info

[![standard-readme compliant](https://img.shields.io/badge/readme%20style-standard-brightgreen.svg?style=flat)](https://github.com/RichardLitt/standard-readme)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

```
Station: E72639 | Starting date: 06:15, Oct 15 2017
        0800    1000    1200    1400    1600    1800    2000    2200    0000    0200    0400    0600    0800
+6.0                                                                                                        
+5.0                                   ~~~~~                                              ~~~~~~            
+4.0                                ~~~     ~~~                                        ~~~      ~~~         
+3.0                             ~~~           ~~~                                  ~~~            ~~       
+2.0                           ~~                 ~~                              ~~                 ~~     
+1.0                         ~~                     ~~                          ~~                     ~~   
+0.0~~                    ~~~                         ~~~                     ~~                         ~~ 
-1.0  ~~~               ~~                               ~~                 ~~                             ~
-2.0     ~~~         ~~~                                   ~~~            ~~                                
-3.0        ~~~~~~~~~                                         ~~~~     ~~~                                  
-4.0                                                              ~~~~~                                     
    0700    0900    1100    1300    1500    1700    1900    2100    2300    0100    0300    0500    0700
```

> Collects and displays tidal information from around the UK.

A command-line interface program written in Java that collects and displays tidal information from monitoring stations around the United Kingdom. It utilizes the Environment Agency's [Tide Gauge API](https://environment.data.gov.uk/flood-monitoring/doc/tidegauge#measures) (in beta) to list tidal information for various stations around the UK, and Google's [Gson](https://github.com/google/gson) for deserializing JSON into Java Objects.

## Install

1. `build`

## Usage

```
$ java Main --help
  
Usage:
  Main --help
  Main list [--search=<id|name|area>]
  Main get <station-id> [options]
  
Options:
  --graph-size=<size> set the size to be small, medium, or large
                      [default: medium]
  --hours=<hours>     get data from the past 1-72 hours
                      [default: 36]
  
Examples:
  Main list --search="port"
  Main get E70124
  Main get E70124 --graph-size=large --hours=60
```

## Maintainer

[Richard Northen](https://github.com/richardnorthen)

## Contribute

Not sure why you'd want to contribute to such a trivial program, but sure, I'll accept PRs that look reasonable.

## License

Uses the [MIT License © 2017 Richard Northen](LICENSE)