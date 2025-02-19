# Report

## Exercise 1 - Transaction, Renting a DVD

**Report**  
* Describe an alternative strategy that could achieve a similar result to the one implemented.

**Answer :**
We could add a version field to the `Inventory` entity and use optimistic locking to prevent concurrent updates. When a user rents a DVD, we increment the version field. If the version field has changed when the user tries to rent the DVD, we know that another user has already rented the DVD and we can return an error.

**Report**
* Explain why managing the concurrency using [@Lock](https://quarkus.io/guides/cdi-reference#container-managed-concurrency) or Java `synchronized` is not a solution.

**Answer :**
@Lock and synchronized are not a solution because can not block with multiple instances of the application running. They block only within the same instance of the application. We need to block at a database level to handle concurrency issues appropriately.

## Exercise 3 - Implement authentication for staff

**Report** Explain why the password storage in Sakila `Staff` table is insecure. Provide a proposal for a more secure password storage.

**Answer :**
The password storage in Sakila `Staff` table is insecure because it uses SHA1 hashing algorithm. SHA 1 is considered weak and vulnerable to collision attacks. [In 2005, researchers demonstrated a collision attack against SHA1 that showed it was possible to create two distinct input messages that produced the same hash value. As a result, SHA1 was officially declared insecure by the National Institute of Standards and Technology (NIST) in 2011.](https://www.keycdn.com/support/sha1-vs-sha256#:~:text=In%202005%2C%20researchers%20demonstrated%20a%20collision%20attack%20against%20SHA1%20that%20showed%20it%20was%20possible%20to%20create%20two%20distinct%20input%20messages%20that%20produced%20the%20same%20hash%20value.%20As%20a%20result%2C%20SHA1%20was%20officially%20declared%20insecure%20by%20the%20National%20Institute%20of%20Standards%20and%20Technology%20(NIST)%20in%202011.) Additionally, the passwords are stored without a salt, wich makes them susceptible to rainbow table attacks.

A solution for more secure password storage, is to use a strong hashing algorithm like bcrypt, agron2 or PBKDF2. Futhermore, add a unique salt for each password.

**Report** Describe the flow of HTTP requests of the above test case and explain:
* What is sent at step 1 to authenticate to the application and how it is transmitted.
  * **Answer :** The user's credentials are sent to the application via an HTTP POST request using form data to the endpoint.
* What is the content of the response at step 2 that his specific to the authentication flow.
  * **Answer :** If the credentials are valid, the server responds with a 302 Found status code. The response includes a session cookie that contains the authentication token.
* What is sent at step 3 to authenticate the user to the application and how it is transmitted.
  * **Answer :** The session cookie is sent automatically by the browser in the HTTP request headers when accessing the protected resource (/hello/me).

Explain why the above test authentication flow is considered insecure, if it was used in a productive environment as is, and what is required to make it secure.

**Answer:**
The test authentication flow is insecure because the credentials and session cookie are transmitted over unencrypted HTTP connection. This makes them vulnerable to interception by attackers.

To make it secure, use HTTPS to protect the credentials and session cookie, and set the `Secure` and `HttpOnly` flags on cookies to prevent them from being accessed via JavaScript and ensure only transmission over HTTPS.

## Exercise 5 - Implement a frontend for rentals

**Report** Discuss the pros and cons of using an approach such as the one promoted by htmx.

**Answer:**
**Pros:**
It's easy to use and eliminates need for complex JavaScript code. 
It improves the user experience by making the application more responsive and interactive.
It reduces the amount of data transferred between the client and server, which can improve performance.

**Cons:**
It may not be suitable for complex applications that require a lot of client-side logic.
It may not be suitable for applications that require a lot of interactivity and real-time updates.
The learning curve may be steep for developers who are not familiar with the technology.
Large applications may require additional tools and libraries to manage complexity.
Server-side rendering may be slower than client-side rendering with many concurrent users.
