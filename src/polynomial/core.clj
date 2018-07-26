(ns polynomial.core
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sgen]
            [clojure.spec.test.alpha :as stest]
            [expound.alpha :as expound]))

(s/def ::number number?)

(s/def ::expr (s/spec (s/cat :op #{'+'}
                             :x #{'x}
                             :num ::number)))

(s/def ::polynomial (s/cat :op #{'*'}
                           :coefficient ::number
                           :expr (s/+ ::expr)))

(s/fdef polynomial->fn
  :args (s/cat :polynomial (s/spec ::polynomial))
  :ret (s/fspec :args (s/cat :x ::number)
                :ret ::number))

(defn polynomial->fn
  "convert a polynomial string to a Clojure function."
  [polynomial]
  (eval (list 'fn '[x] polynomial)))

(s/fdef satisfy
  :args (s/or :arity-2 (s/cat :pred ifn?
                              :x ::number)
              :arity-3 (s/cat :pred ifn?
                              :x ::number
                              :n pos-int?))
  :ret any?)

(defn satisfy
  "Generate `n` sample polynomials (default 10), evaluate each polynomial at `x`,
  and check if it satisfies `pred`.

  e.g. (satisfy zero? 1 10) will generate 10 polynomials and check if each
  polynomial is zero when x = 1."
  ([pred x] (satisfy pred x 10))
  ([pred x n]
   (for [[[polynomial] f] (s/exercise-fn `polynomial->fn n)]
     {:polynomial polynomial
      :satisfied? (pred (f x))})))

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
