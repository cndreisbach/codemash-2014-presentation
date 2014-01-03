# TodoFRP

An implementation of [TodoMVC][1] using [Hoplon][2] and [Liberator][3].

## Usage

Install [boot][4]. Then in a terminal:

```
$ boot watch hoplon
```

In another terminal:

```
$ lein run
```

* HTML and JavaScript files will be created in the `resources/public` directory.
* The web server will run at http://localhost:3000/.

[1]: http://todomvc.com
[2]: http://github.com/tailrecursion/hoplon
[3]: http://clojure-liberator.github.io/liberator/
[4]: https://github.com/tailrecursion/boot

## License

```
Copyright (c) Alan Dipert, Micha Niskin, and Clinton Dreisbach. All rights
reserved. The use and distribution terms for this software are
covered by the Eclipse Public License 1.0
(http://opensource.org/licenses/eclipse-1.0.php) which can be
found in the file epl-v10.html at the root of this
distribution. By using this software in any fashion, you are
agreeing to be bound by the terms of this license. You must not
remove this notice, or any other, from this software.
```
