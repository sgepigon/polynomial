(ns polynomial.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sgen]
            [clojure.spec.test.alpha :as stest]
            [expound.alpha :as expound]))

(s/def ::real (s/double-in :infinite? false :NaN? false))
(s/def ::int-or-real (s/or :int int? :real ::real))

(s/def ::expr (s/spec (s/cat :op #{'+}
                             :x #{'x}
                             :num ::int-or-real)))

(s/def ::polynomial (s/cat :op #{'*}
                           :coefficient ::int-or-real
                           :expr (s/+ ::expr)))

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
