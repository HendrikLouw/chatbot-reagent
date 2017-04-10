(ns chatbot.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [chatbot.core-test]))

(doo-tests 'chatbot.core-test)
