# walmartlabs

find loss or gains from stores

* multi-threads for performant file IO! (not that it matters when all harddrives are slow, but some big servers might has SSD-Raids ... )

## Compile

lein bin

## Linux/OSX Usage

./target/find-loss-gain -t 1[more if multi-core machine] -d ./resources/data-full/ > res.log

## Win Usage

lein run -- -t 1[more if multi-core machine] -d resources/data-full/ > res.log

