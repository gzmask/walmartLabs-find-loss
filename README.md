# walmartlabs

find loss or gains from stores

* multi-threads for performant file IO! (not that it matters when all harddrives are slow, but some big servers might has SSD-Raids ... ) Go blocks are lightweight. Try over 1000!
* you will also find out from the data-set that what kind of nasty stuff walmart is selling us. 

## Compile

lein bin

## Help

lein run -- -h

  -h, --help
  
  -p, --price price-file    prices.csv         find-loss-gain -p [price-file-name-in-directory].

  -d, --directory path      ./resources/data/  find-loss-gain -d [path-to-directoy].
  
  -t, --threads thread-num  1                  find-loss-gain -t [thread-num].

## Linux/OSX Usage

./target/find-loss-gain -t 1[more if multi-core machine] -d ./resources/data-full/ > res.log

## Win Usage

lein run -- -t 1[more if multi-core machine] -d resources/data-full/ > res.log

