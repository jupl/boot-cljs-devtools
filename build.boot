(set-env!
 :source-paths #{"src"}
 :dependencies '[[boot/core        "2.2.0"  :scope "provided"]
                 [adzerk/bootlaces "0.1.13" :scope "test"]])

(require '[adzerk.bootlaces :refer :all])

(def +version+ "0.1.0")

(bootlaces! +version+)

(task-options!
 pom {:project     'jupl/boot-cljs-devtools
      :version     +version+
      :description "Boot task to add Chrome DevTool enhancments for CLJS."
      :url         "https://github.com/jupl/boot-cljs-devtools"
      :scm         {:url "https://github.com/jupl/boot-cljs-devtools"}
      :license     {"MIT" "https://opensource.org/licenses/MIT"}})
