
## DataStore 介绍

Jetpack DataStore 是一种数据存储解决方案，允许您使用协议缓冲区存储键值对或类型化对象。DataStore 使用 Kotlin 协程和 Flow 以异步、一致的事务方式存储数据。

DataStore 是用来替代 SharePreference 的，如果你的项目使用的是 SharePreference，那么建议你迁移到 DataStore

DataStore 提供两种实现 PreferenceDataStore 和 Proto DataStore

### PreferenceDataStore

使用键值对的方式存储数据，不能确保类型安全

### Proto DataStore

将数据作为自定义类型进行存储，可以确保类型安全

## DataStore 使用

### PreferenceDataStore

#### 依赖

```
  implementation("androidx.datastore:datastore:1.0.0-beta01")

```

#### 代码

1. 初始化 DataStore

首先我们在 App 中初始化 dataStore，

```
class App: Application() {
    companion object{
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    }
}
```

val Context.dataStore 这句代码调用后我们就可以再全局使用 dataStore 对象了

2. 初始化 key

```
val EXAMPLE_COUNTER = intPreferencesKey("example_counter")
```

3. PreferenceDataStore 存储数据

```
dataStore.edit { settings ->
            val currentCounterValue = settings[EXAMPLE_COUNTER] ?: 0
            settings[EXAMPLE_COUNTER] = currentCounterValue + 1
        }
```

存储数据的代码每次存储一次数据，EXAMPLE_COUNTER 对应的数据就会加一

4. PreferenceDataStore 获取数据

```
 val exampleCounterFlow: Flow<Int> = dataStore.data
                .map { preferences ->
                    logEE(preferences[EXAMPLE_COUNTER].toString())//打印数据
                    preferences[EXAMPLE_COUNTER] ?: 0
                }
```

### ProtoDataStore

Proto DataStore 实现使用 DataStore 和协议缓冲区将类型化的对象保留在磁盘上。

所谓的协议缓冲区，其实就是指的流

官网对 ProtoDataStore 的集成流程写的不全，所以我找到了这篇文章进行学习：

[文章入口](https://mp.weixin.qq.com/s/lM808MxUu6tp8zU8SBu3sg)

#### 配置集成

1. 集成依赖

```
 implementation("androidx.datastore:datastore-preferences:1.0.0-beta01")
 implementation "com.google.protobuf:protobuf-javalite:3.10.0"
```

2. 添加 protobuf
   protobuf 与 android 和 dependencies 同级

```
protobuf {
    // 设置 protoc 的版本
    protoc {
        // //从仓库下载 protoc 这里的版本号需要与依赖 com.google.protobuf:protobuf-javalite:xxx 版本相同
        artifact = 'com.google.protobuf:protoc:3.10.0'
    }
    generateProtoTasks {
        all().each { task ->
            task.builtins {
                java {
                    option "lite"
                }
            }
        }
    }

    // 默认生成目录 $buildDir/generated/source/proto 通过 generatedFilesBaseDir 改变生成位置
    generatedFilesBaseDir = "$projectDir/src/main"
}
```

3. 添加源码目录

```
 sourceSets {
        main {
            proto {
                // proto 文件默认路径是 src/main/proto
                // 可以通过 srcDir 修改 proto 文件的位置
                srcDir 'src/main/proto'
            }
        }
    }
```

4. 添加 gradle 插件

```
  id "com.google.protobuf" version "0.8.12"
```

配置集成完成

#### 代码集成

1. 编写 proto 文件并编译
   ![](https://files.mdnice.com/user/15648/43efaecd-5a27-4fcc-aebf-e272572ccad8.png)
2. 编写 proto 文件

```
syntax = "proto3";

option java_package = "com.ananananzhuo.datastoredemo";//必须是自己的包名
option java_multiple_files = true;

message Settings {
  int32 example_counter = 1;
}
```

3. 编写 java 文件 SettingsSerializer
   这个文件在编译后会被移动到 src/main/debug/java/com.ananananzhuo.datastoredemo 目录下

```
object SettingsSerializer : Serializer<Settings> {
    override val defaultValue: Settings = Settings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): Settings {
        try {
            return Settings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(
        t: Settings,
        output: OutputStream
    ) = t.writeTo(output)
}

val Context.settingsDataStore: DataStore<Settings> by dataStore(
    fileName = "settings.pb",
    serializer = SettingsSerializer
)
```

4. 存储数据
   存储数据一定要在协程中调用

```
 suspend fun incrementCounter() {
        settingsDataStore.updateData { currentSettings ->
            currentSettings.toBuilder()
                .setExampleCounter(currentSettings.exampleCounter + 1)
                .build()
        }
    }
```

5. 获取数据

```
val exampleCounterFlow: Flow<Int> = settingsDataStore.data
                .map { settings ->
                    logEE(settings.exampleCounter.toString())//获取数据并打印
                    settings.exampleCounter
                }
```