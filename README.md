# Micronaut-QuerySQL

Micronaut support for [QuerySQL](https://github.com/requery/requery)

## HowTo To install
```kotlin
dependencies {
    //...
    implementation("com.github.ryarnyah:micronaut-querydsl:${micronautKotlinRequeryVersion}")
    //...
}
```

## HowToUse
### Kotlin version
```kotlin
@Singleton
open class TestService(
    private val sqlQueryFactory: MicronautSQLQueryFactory
) {
    @Transactional
    open fun insert(entity: TestData) {
        sqlQueryFactory.insert(QTestData.testData).populate(entity).execute()
    }
    
    open fun findById(id: String): TestData? {
        return sqlQueryFactory.select(QTestData.testData)
            .from(QTestData.testData)
            .where(QTestData.testData.uid.eq(id))
    }
}
```
### Java version
```java
@Singleton
public class TestService {
    
    private final MicronautSQLQueryFactory sqlQueryFactory;

    public TestService(MicronautSQLQueryFactory sqlQueryFactory) {
        this.entityDataStore = entityDataStore;
    }

    @Transactional
    public void save(TestData entity) {
        sqlQueryFactory.insert(QTestData.testData).populate(entity).execute();
    }
    
    public TestData findById(String id) {
        return sqlQueryFactory.select(QTestData.testData)
                .from(QTestData.testData)
                .where(QTestData.testData.uid.eq(id));
    }
}
```

## Current status
- [X] Add basic micronaut support
- [X] Add Java Support
- [ ] Make more configurable
