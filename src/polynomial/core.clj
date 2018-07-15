(ns polynomial.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sgen]
            [clojure.spec.test.alpha :as stest]
            [expound.alpha :as expound]))

(s/def ::real (s/double-in :infinite? false :NaN? false))
(s/def ::int-or-real (s/or :int int? :real ::real))

(spec/def ::infix (spec/cat :x #{'x}
                            :op #{'+ '-}
                            :num ::int-or-real))

(spec/def ::prefix (spec/spec (spec/cat :op #{'+ '-}
                                        :x #{'x}
                                        :num ::int-or-real)))

(spec/def ::expr (spec/or :basic ::prefix
                          :complex (spec/spec (spec/cat :op #{'*}
                                                        :basic ::prefix
                                                        :complex ::expr))))

(spec/def ::polynomial (spec/cat :op #{'*}
                                 :coefficient ::int-or-real
                                 :expr ::expr))
(s/fdef polynomial->fn
  :args (s/cat :polynomial (s/spec ::polynomial))
  :ret (s/fspec :args (s/cat :x ::real)
                :ret number?))

(defn polynomial->fn
  "convert a polynomial string to a Clojure function."
  [polynomial]
  (eval (list 'fn '[x] polynomial)))

(comment

  ;; get the seed of the last output
  (-> *1 first :clojure.spec.test.check/ret :seed)

  ;; check previously failing seeds
  (for [seed [1531450911777 1531452083625 1531452439163]]
    (stest/check `polynomial->fn {:clojure.spec.test.check/opts {:seed seed}}))

  (set! s/*explain-out* (expound/custom-printer {:show-valid-values? true
                                                 :print-specs? false
                                                 :theme :figwheel-theme}))
  ;; Put your cursor on start of this line (after the above sexp) and type `SPC m e e`
  )
