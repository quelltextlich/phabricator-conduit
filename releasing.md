Release
========

To do a `Phabricator Conduit Java Connector` release, run

```
mvn release:prepare -Prelease,with-hidden-modules -Dresume=false && \
mvn release:perform -Prelease,with-hidden-modules
```
