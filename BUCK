include_defs('//lib/maven.defs')
include_defs('//lib/prolog/prolog.defs')

JACKSON_REV = '2.1.1'
maven_jar(
  name = 'jackson-core',
  id = 'com.fasterxml.jackson.core:jackson-core:%s' % JACKSON_REV,
  license = 'Apache2.0',
)

maven_jar(
  name = 'jackson-databind',
  id = 'com.fasterxml.jackson.core:jackson-databind:%s' % JACKSON_REV,
  license = 'Apache2.0',
)

maven_jar(
  name = 'jackson-annotations',
  id = 'com.fasterxml.jackson.core:jackson-annotations:%s' % JACKSON_REV,
  license = 'Apache2.0',
)

maven_jar(
  name = 'jackson-dataformat-yaml',
  id = 'com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:%s' % JACKSON_REV,
  license = 'Apache2.0',
)

maven_jar(
  name = 'gitective-core',
  id = 'org.gitective:gitective-core:0.9.9',
  license = 'Apache2.0',
)

EXTERNAL_DEPS = [
  ':gitective-core',
  ':jackson-core',
  ':jackson-databind',
  ':jackson-annotations',
  ':jackson-dataformat-yaml',
]

java_library(
  name = 'gerrit-owners-common-lib',
  srcs = glob([
    'gerrit-owners-common/src/main/java/**/*.java',
  ]),
  deps = [
    '//:plugin-lib',
  ] + EXTERNAL_DEPS,
)

java_library(
  name = 'gerrit-owners-lib',
  srcs = glob([
    'gerrit-owners/src/main/java/**/*.java',
  ]),
  deps = [
    ':gerrit-owners-common-lib',
    '//:plugin-lib',
    '//lib/prolog:prolog-cafe',
    '//gerrit-server/src/main/prolog:common',
  ] + EXTERNAL_DEPS,
)

prolog_cafe_library(
  name = 'gerrit-owners-prolog-rules',
  srcs = glob(['gerrit-owners/src/main/prolog/*.pl']),
  deps = [
    '//gerrit-server/src/main/prolog:common',
    ':gerrit-owners-lib',
  ],
)

gerrit_plugin(
  name = 'owners',
  srcs = [],
  manifest_entries = [
    'Implementation-Title: Gerrit OWNERS plugin',
    'Implementation-URL: https://github.com/vadims/gerrit-owners',
    'Gerrit-PluginName: owners',
  ],
  deps = [
    ':gerrit-owners-prolog-rules',
  ] + EXTERNAL_DEPS,
)

gerrit_plugin(
  name = 'owners-autoassign',
  srcs = glob([
    'gerrit-owners-autoassign/src/main/java/**/*.java',
  ]),
  manifest_entries = [
    'Implementation-Title: Gerrit OWNERS autoassign plugin',
    'Implementation-URL: https://github.com/vadims/gerrit-owners',
    'Gerrit-PluginName: owners-autoassign',
  ],
  deps = [
    ':gerrit-owners-common-lib',
  ] + EXTERNAL_DEPS,
)
