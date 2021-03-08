
(ns translate-demo
  (:require [reagent.core :as r]
            [roam.datascript :as rd]))

(def original-text (r/atom nil))
(def translation (r/atom nil))
(def api-url "https://roam-translate.herokuapp.com/translate")

(defn trim [s] (subs s 4 (- (count s) 3)))

(defn get-text-from-block [block-id]
  (reset! original-text 
          (-> 
            (rd/q '[:find ?string 
                      :in $ ?uid 
                      :where [?b :block/uid ?uid]
                             [?b :block/string ?string]]
        		     block-id)
            str
            trim
            )))

(defn fetch-translation [lang text]
	(-> (.fetch js/window "https://roam-translate.herokuapp.com/translate" 
                {:method "POST"
                 :headers {:content-type "application/json"}
                 :body (.stringify js/JSON
                                   {:lang lang :text text})})
  (.then #(.json %))
  (.then #(reset! translation (.-result %)))
))

(defn render-translation [text]
   	[:span "Translation: " text])

(defn main [_ lang [_ block-id]]
   (get-text-from-block block-id)
   (when @original-text (fetch-translation lang @original-text))
   (render-translation @translation))


