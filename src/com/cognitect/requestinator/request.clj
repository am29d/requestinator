(ns com.cognitect.requestinator.request
  "A library that defines a set of operations for generating requests.
  This happens in two steps. First, we generate a sequence of
  potentially abstract requests to be executed. Abstract, because it
  may contain placeholders that need to be filled in at runtime, for
  instance based on the results of previous requests. At runtime,
  request templates are finalized into actual requests.")

;; TODO: Consider whether putting a method to get Transit
;; readers/writers on this protocol would be helpful.
(defprotocol Generator
  (-generate [this params]
    "Return a lazy sequence of request templates."))

(defprotocol Template
  (-fill-in [this params]
    "Fills in the 'holes' in a request template with information from
    params to produce an actionable request map."))

;; We consolidate calls through these functions so that we
;; have a central place to add things like logging.
(defn generate
  "Generate a lazy sequence of request templates. Call this in preference
  to the protocol method."
  [generator params]
  (-generate generator params))

(defn fill-in
  "Fill in a request template to make a concrete request based on the
  information in the `params` map. Call this in preference to the
  protocol method."
  [template params]
  (-fill-in template params))

(defmulti dynamic-param-op (fn [op context & args] op))

