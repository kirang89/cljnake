(ns cljnake.runner
    (:require [doo.runner :refer-macros [doo-tests]]
              [cljnake.core-test]))

(doo-tests 'cljnake.core-test)
