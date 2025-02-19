# Report

TODO: Replace each section content with an appropriate answer according to instructions.
## Exercice 1 - Entity mappings

**Report** The tests provided that validate updating entities are using `EntityManager.flush()` and `EntityManager.clear()`) (e.g. `ActorRepositoryTest.testUpdateActor`).

* Describe precisely from the perspective of SQL statements sent to the database, the difference between:
    * `ActorRepositoryTest.testUpdateActor`
    * `ActorRepositoryTest.testUpdateActorWithoutFlushAndClear`
    * `ActorRepositoryTest.testUpdateActorWithoutClear`

> For testUpdateActor the statements are in order:
>1. "INSERT" to create new actor
>2. UPDATE to update the actor, the flush() sends the SQL statement to the database (In reality an INSERT because the actor does not yet exist in the database)
>3. SELECT to retrieve the updated actor from the database

>For testUpdateActorWithoutFlushAndClear the statements are in order:
>1. "INSERT" to create new actor
>2. "UPDATE" to update the actor in the persistence context/cache
>3. "SELECT" to retrieve the updated actor from the persistence context/cache

>For testUpdateActorWithoutClear the statements are in order:
>1. "INSERT" to create new actor
>2. UPDATE to update the actor, the flush() sends the SQL statement to the database (In reality an INSERT because the actor does not yet exist in the database)
>3. "SELECT" to retrieve the updated actor from the persistence context/cache (the updated actor is in cache because no clear() was called)

> Thanks to the persistance context, the statements with "" are theorical and never really done because the information needed is in cache so no real SQL statement has to be sent to the database.

* Explain the behavior differences and why it occurs.

>The EntityManager method flush() synchronizes/forces the persistence context with the underlying database. It sends the SQL statements immediately to the database.
> 
>The EntityManager method clear() clears the persistence context/cache. This means that subsequent queries will not be able to retrieve the updated entity from the cache so new queries will be made to the database.

>Tests without flush() causes the changes not to be immediately written to the database so the changes are still only in the persistence context and tests without clear() means the persistence context retains the updated entity, so subsequent reads do not need to hit the database.

>The usage (or not) of these methods in the 3 tests explain why the behavior differs depending on which test is being run.

Hints: run the tests using the debugger, look at the SQL statements in the log.

## Exercice 2 - Querying

**Report** on the query language that you prefer and why.

> Personally, I prefer JPQL because it's, for me at least, a good balance between readability, portability, and abstraction while still allowing you to write queries in a way that closely ressemble native SQL (and I clearly prefer the SQL syntax compared to Criteria syntax which I find ugly and not easily readable).

>While ressembling SQL, JPQL still provides abstraction becauses it uses entity names and attributes which makes database statements easier. In JPQL, for example, between Actor and Film we just have to join on the films attribute in Actor compared to native SQL where we first have to join the bridge table film_actor and then we can join the film table.

>Another advantage comes from the fact that JPQL is, thanks to abstraction, database-agnostic, making it way more portable than native SQL. 

> Finally, a disadvantage I found is the fact that JPQL can be less performant for complex queries compared to native SQL which could be an issue if we're building an app oriented towards high performance.