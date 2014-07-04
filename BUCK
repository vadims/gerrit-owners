include_defs('//lib/maven.defs')
include_defs('//lib/prolog/prolog.defs')

JACKSON_REV = '2.1.1'
maven_jar(
  name = 'jackson-core',
  id = 'com.fasterxml.jackson.core:jackson-core:%s' % JACKSON_REV,
  #bin_sha1 = '82ad1c5f92f6dcc6291f5c46ebacb975eaa844de',
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

java_library(
  name = 'base-prolog-rules',
  srcs = glob([
    'gerrit-owners-common/src/main/java/**/*.java',
    'gerrit-owners/src/main/java/**/*.java',
  ]),
  deps = [
    '//lib/prolog:prolog-cafe',
    '//gerrit-server/src/main/prolog:common',
    ':gitective-core',
    ':jackson-core',
    ':jackson-databind',
    ':jackson-annotations',
    ':jackson-dataformat-yaml',
    '//:plugin-lib',
  ],
)

prolog_cafe_library(
  name = 'owners-prolog-rules',
  srcs = glob(['gerrit-owners/src/main/prolog/*.pl']),
  deps = [
    '//gerrit-server/src/main/prolog:common',
    ':base-prolog-rules',
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
    ':owners-prolog-rules',
  ],
)
