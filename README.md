# DOMA3 と Spring Security でログイン認証を実装する

## 事前準備

### 必要な拡張機能をインストールする

```bash
code --install-extension humao.rest-client
code --install-extension vmware.vscode-boot-dev-pack
code --install-extension cweijan.vscode-postgresql-client2
code --install-extension yzhang.markdown-all-in-one
```

### Spring Initializer で新規プロジェクトを作成する

以下を選択する

- spring-boot-devtools
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-validation
- lombok

初期化した後の`Gradle.build`は下記の通り。

```gradle
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.4.5'
	id 'io.spring.dependency-management' version '1.1.7'
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot dependencies
	developmentOnly 'org.springframework.boot:spring-boot-devtools'
	implementation 'org.springframework.boot:spring-boot-starter-security'
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	implementation 'org.springframework.boot:spring-boot-starter-web'

	// lombok
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'

	// test
	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testImplementation 'org.springframework.security:spring-security-test'
}

tasks.named('test') {
	useJUnitPlatform()
}

```

### Doma3 を導入する

`build.gralde`にDoma系のライブラリを追記する。
※詳細は[Eclipse + Java + Gradle の環境で Doma を動かす](https://qiita.com/nakamura-to/items/9e05fe00be9d4d629fdc)を参照してください。

```gradle:build.gradle
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.4.5'
	id 'io.spring.dependency-management' version '1.1.7'

    id 'com.diffplug.eclipse.apt' version '3.44.0' 		// Eclipse のアノテーションプロセッシング関連の設定ファイルを生成するプラグイン
    id 'org.domaframework.doma.compile' version '2.0.0' // Doma を使ったビルドをサポートするプラグイン
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot dependencies
	developmentOnly 'org.springframework.boot:spring-boot-devtools'
	implementation 'org.springframework.boot:spring-boot-starter-security'
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	implementation 'org.springframework.boot:spring-boot-starter-web'

	// lombok
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'

	// testing
	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testImplementation 'org.springframework.security:spring-security-test'

	// Doma	   
    implementation 'org.seasar.doma:doma-core'  			    // Doma の核となるライブラリ。ビルド時も実行時も必要。
    implementation'org.seasar.doma:doma-slf4j' 				    // Doma のログをSLF4J経由で出力するライブラリ。
	annotationProcessor 'org.seasar.doma:doma-processor:3.6.0'	// Doma のアノテーションプロセッサー。ビルド時のみ必要。

	// logback(SLF4J)
    runtimeOnly 'ch.qos.logback:logback-classic'    		    // SLF4J の実装ライブラリ。

	// postgresql JDBCドライバ
	runtimeOnly 'org.postgresql:postgresql'					    // データベース接続用のJDBCドライバ。
}

tasks.named('test') {
	useJUnitPlatform()
}

// Eclipse 関連の設定
eclipse {
    // .classpath ファイルに関する設定
    classpath {
        file {
            whenMerged { classpath ->
                // アノテーションプロセッサーで生成したソースコードを保存するフォルダ
                def folder = new org.gradle.plugins.ide.eclipse.model.SourceFolder(".apt_generated", "bin/main")
                // クラスパスに追加
                classpath.entries.add(folder)
                // 存在しなければ作成
                def dir = file(folder.path)
                if (!dir.exists()) {
                    dir.mkdir()
                }
            }
        }
    }
    // .project ファイルに関する設定
    project {
        buildCommand 'org.eclipse.buildship.core.gradleprojectbuilder'
        natures 'org.eclipse.buildship.core.gradleprojectnature'
    }
    // Eclipse から "Refresh Gradle Project" を呼び出した時に実行されるタスク
    synchronizationTasks 'cleanEclipse', 'eclipse'
}
```

ただし今回は IDE ではなく、VSCodeを利用しているため、このままでは動作しない。

### `gralde eclipse`を実行する

`gralde eclipse`を実行し、下記のファイルが作成されることを確認する。

- .settings/org.eclipse.jdt.apt.core.prefs
- .settings/org.eclipse.jdt.core.prefs
- .classpath
- .factorypath
- .project

その後下記のエラーが出ている場合は、`.settings/org.eclipse.buildship.core.prefs`を手動で作成してください。

>Missing Gradle project configuration file: .settings/org.eclipse.buildship.core.prefs

`.settings/org.eclipse.buildship.core.prefs`には下記の内容を記入しておいてください。

```
connection.project.dir=
eclipse.preferences.version=1
```

ここで一旦`gradle build`を実行し、成功するか確認してください。
問題なければ、次へ進みます。

※エラーが出る場合は `gradle cleanEclipse eclipse` を実行し、設定ファイルを再生成してみてください。

### doma code-gen を導入する

[Doma CodeGen プラグイン](https://doma.readthedocs.io/ja/stable/codegen/#doma-codegen-plugin)を用いてDomaソース一式を作成します。

まずは対象のテーブルを作成します。

```sql
CREATE TABLE user_admin (
    user_id VARCHAR(100) PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

その後、`build.gradle`に Doma CodeGen の設定を追記します。

```gradle:build.gradle
// buildscriptを追加
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'org.postgresql:postgresql:42.7.5' // 利用するJDBCドライバを指定する
    }
}


plugins {
	id 'java'
	id 'org.springframework.boot' version '3.4.5'
	id 'io.spring.dependency-management' version '1.1.7'

    id 'com.diffplug.eclipse.apt' version '4.3.0' 		
    id 'org.domaframework.doma.compile' version '3.0.1'

    id 'org.domaframework.doma.codegen' version '3.0.0' // doma-codegen プラグインを追加する
}

group = 'com.example'
version = '0.0.1-SNAPSHOT'

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot dependencies
	developmentOnly 'org.springframework.boot:spring-boot-devtools'
	implementation 'org.springframework.boot:spring-boot-starter-security'
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	implementation 'org.springframework.boot:spring-boot-starter-web'

	// lombok
	compileOnly 'org.projectlombok:lombok'
	annotationProcessor 'org.projectlombok:lombok'

	// testing
	testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testImplementation 'org.springframework.security:spring-security-test'

	// Doma	   
    implementation 'org.seasar.doma:doma-core:3.6.0'
    implementation'org.seasar.doma:doma-slf4j:3.6.0'
	implementation 'org.seasar.doma:doma-processor:3.6.0'

	// logback(SLF4J)
    runtimeOnly 'ch.qos.logback:logback-classic'

	// postgresql JDBCドライバ
	runtimeOnly 'org.postgresql:postgresql'
}

tasks.named('test') {
	useJUnitPlatform()
}

eclipse {
    classpath {
        file {
            whenMerged { classpath ->
                def folder = new org.gradle.plugins.ide.eclipse.model.SourceFolder(".apt_generated", "bin/main")
                classpath.entries.add(folder)
                def dir = file(folder.path)
                if (!dir.exists()) {
                    dir.mkdir()
                }
            }
        }
    }
    project {
        buildCommand 'org.eclipse.buildship.core.gradleprojectbuilder'
        natures 'org.eclipse.buildship.core.gradleprojectnature'
    }
    synchronizationTasks 'cleanEclipse', 'eclipse'
}

// SQLファイルを'bin/default/META-INF'にコピーするタスクを定義しておく。
task _copySqls(type: Copy) {
  from 'src/main/resources/META-INF'
  into 'bin/default/META-INF'
}
compileJava.dependsOn _copySqls //compileJavaタスクに依存させておく。

// Doma-codegen タスクを定義する
domaCodeGen {
    // make an arbitrary named block
    dev {
        // JDBC url
        url = 'jdbc:postgresql:postgres'
        // JDBC user
        user = 'postgres'
        // JDBC password
        password = 'postgres'
        // configuration for generated entity source files
        entity {
          packageName = 'com.example.demo.doma'
        }
        // configuration for generated DAO source files
        dao {
          packageName = 'com.example.demo.doma'
        }
    }
}
```

追記後、`gralde domagenDevAll`を実行し、各種ソースコードを生成してください。

### Doma が動作するまで

VSCodeで操作していると、Domaが安定しない。。。
その場合は下記を確認してみてください。

-  `gradle cleanEclipse eclipse` を実行して、設定ファイルを再生成する。
-  設定ファイルの内容を確認し、編集する。

### 簡単なAPIを作成する

管理ユーザ作成用APIを作成します。

```java:AdminContoroller.java
package com.example.demo.domain.user.admin;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.domain.user.admin.entity.Admin;


@RestController
@RequestMapping("/api/user/admin")
public class AdminContoroller {

    private final AdminRepository adminRepository;
    private final AdminService adminService;

    AdminContoroller(AdminRepository adminRepository, AdminService adminService) {
        this.adminRepository = adminRepository;
        this.adminService = adminService;
    }
    
    @PostMapping("/create")
    public Admin create(@Validated @RequestBody Admin entity) {
        String encodedPassword = adminService.encodePassword(entity.getPassword());
        adminRepository.create(entity.getUserId(), entity.getUserName(), entity.getEmail(), encodedPassword);
        return entity;
    }
}

```

```java:AdminRepository.java
package com.example.demo.domain.user.admin;

import org.springframework.stereotype.Repository;

import com.example.demo.doma.dao.UserAdminDao;
import com.example.demo.doma.entity.UserAdmin;

@Repository
public class AdminRepository {

    private final UserAdminDao userAdminDao;

    public AdminRepository(UserAdminDao userAdminDao) {
        this.userAdminDao = userAdminDao;
    }

    public void create(String userId, String userName, String email, String password) {
        
        UserAdmin userAdnmin = new UserAdmin();
        userAdnmin.setUserId(userId);
        userAdnmin.setUserName(userName);
        userAdnmin.setEmail(email);
        userAdnmin.setPasswordHash(password);

        userAdminDao.insert(userAdnmin);
    }

    public UserAdmin findByUserId(String userId) {
        return userAdminDao.selectById(userId);
    }
}
```

```java:AdminService.java

package com.example.demo.domain.user.admin;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    
    private final PasswordEncoder passwordEncoder;
    AdminService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    public String encodePassword(String password) {
        return passwordEncoder.encode(password);
    }

}
```

```java:UserAdminListener.java
package com.example.demo.doma.entity;

import java.time.LocalDateTime;

import org.seasar.doma.jdbc.entity.EntityListener;
import org.seasar.doma.jdbc.entity.PostDeleteContext;
import org.seasar.doma.jdbc.entity.PostInsertContext;
import org.seasar.doma.jdbc.entity.PostUpdateContext;
import org.seasar.doma.jdbc.entity.PreDeleteContext;
import org.seasar.doma.jdbc.entity.PreInsertContext;
import org.seasar.doma.jdbc.entity.PreUpdateContext;

/**
 * 
 */
public class UserAdminListener implements EntityListener<UserAdmin> {

    @Override
    public void preInsert(UserAdmin entity, PreInsertContext<UserAdmin> context) {
        LocalDateTime now = LocalDateTime.now();    //リスナーでタイムスタンプを発行できるようにしておく。
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
    }

    @Override
    public void preUpdate(UserAdmin entity, PreUpdateContext<UserAdmin> context) {
    }

    @Override
    public void preDelete(UserAdmin entity, PreDeleteContext<UserAdmin> context) {
    }

    @Override
    public void postInsert(UserAdmin entity, PostInsertContext<UserAdmin> context) {
    }

    @Override
    public void postUpdate(UserAdmin entity, PostUpdateContext<UserAdmin> context) {
    }

    @Override
    public void postDelete(UserAdmin entity, PostDeleteContext<UserAdmin> context) {
    }
}

```

REST Client で下記リクエストを送信してみると無事登録できました。

```bash
# ユーザー作成APIを呼び出す
POST http://localhost:8080/api/user/admin/create HTTP/1.1
content-type: application/json

{
    "userId": "admin",
    "userName": "管理者",
    "password": "admin123",
    "email": "test@example.com"
}
```




今回作成したデモアプリは公開しているので、気になる人はソースを確認してください。


[DOMA3 と Spring Security でログイン認証を実装する](https://github.com/kamo-shika/doma-spring-security)

### 参考文献

- [Eclipse + Java + Gradle の環境で Doma を動かす](https://qiita.com/nakamura-to/items/9e05fe00be9d4d629fdc)
- [Java - Visual Studio CodeでDomaの注釈処理を使う](https://bifutek.hatenablog.com/entry/2022/10/20/005726)
- [Doma CodeGen プラグイン](https://doma.readthedocs.io/ja/stable/codegen/#doma-codegen-plugin)

