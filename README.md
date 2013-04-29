# Gerrit OWNERS Plugin

This plugin provides a Prolog predicate `add_owner_approval/3` that appends `label('Owner-Approval', need(_))` to a provided list.

Owner approval is determined based on OWNERS files located in the same repository. They are resolved against the state present in the existing master branch.

The OWNERS files are represented by the following YAML structure:

```yaml
inherited: true
owners:
- user-a@example.com
- user-b@example.com
```

This translates to inheriting owner email address from any parent OWNER files and to allow `user-a@example.com` and `user-b@example.com` to approve the change.

The plugin analyzes the latest patch set by looking at each patch and building an OWNERS hierarchy. It stops once it finds an OWNERS file that has “inherited” set to false (by default it’s true.)

For example, imagine the following tree:

```
/OWNERS
/example/src/main/OWNERS
/example/src/main/java/com/example/foo/Foo.java
/example/src/main/resources/config.properties
/example/src/test/OWNERS
/example/src/test/java/com/example/foo/FooTest.java
```

If you submit a patch set that changes `/example/src/main/java/com/example/foo/Foo.java` then the plugin will first open `/example/src/main/OWNERS` and if inherited is set to true combine it with the owners listed in `/OWNERS`.

If for each patch there is a reviewer who gave a `Code-Review +2` then the plugin will not add any labels,
otherwise it will add `label('Owner-Approval', need(_))`.

Here’s a sample rules.pl that uses this predicate to enable the submit rule.

```prolog
submit_rule(S) :-
  gerrit:default_submit(D),
  D =.. [submit | Ds],
  findall(U, gerrit:commit_label(label('Code-Review', 2), U), Approvers),
  gerrit_owners:add_owner_approval(Approvers, Ds, A),
  S =.. [submit | A].
```

## Auto assigner

There is a second plugin, `gerrit-owners-autoassign` which depends on `gerrit-owners`. It will automatically assign
all of the owners to review a change when it's created or updated.
