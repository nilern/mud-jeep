# mud-jeep

mud-jeep provides a migration path back from Midje to clojure.test

# Prior Art

* [circleci/translate-midje](https://github.com/circleci/translate-midje)
    - Described in [Rewriting Your Test Suite in Clojure in 24 hours](https://circleci.com/blog/rewriting-your-test-suite-in-clojure-in-24-hours/)
* [metosin/testit](https://github.com/metosin/testit)
* [jimpil/fudje](https://github.com/jimpil/fudje)

# Source Conversion

* [ ] Convert top-level `fact`/`facts` to `deftest` like `translate-midje` but using
      [rewrite-clj](https://github.com/xsc/rewrite-clj) as the CircleCI blog post recommends.
* [ ] Convert `midje.*` requires in ns-form to `mud-jeep.*` requires.

# Normal Library Stuff

* [ ] `fact` and `facts` that (unlike `testit` or `fudje`) can be nested and contain checking arrows in semi-arbitrary
      places. Macroexpand to `testing` and some more powerful form of `is` (like NUnit `Assert.That`).
    - [ ] Support `provided` clauses like (`fudje` does).
    - [ ] Support `lupapalvelu.factlet/fact(s)*` (need to return the value that is being checked instead of a boolean
          like `fact(s)` or `is`).
* [ ] Support `against-background`.
* [ ] Support various arrows and checkers, like all the prior art mentioned above. Need to be Midje-compatible (unlike
      `testit` or `translate-midje`).
* [ ] Support `testable-privates`.
