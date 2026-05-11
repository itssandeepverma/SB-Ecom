# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.ecommerce.sb-ecom' is invalid and this project uses 'com.ecommerce.sb_ecom' instead.

# Getting Started

## Basic Flow Notes

### Application startup

1. The application starts from `SbEcomApplication`.

```java
SpringApplication.run(SbEcomApplication.class, args);
```

2. `@SpringBootApplication` tells Spring Boot to scan the package `com.ecommerce.sb_ecom`.

3. Spring finds and creates objects for classes marked with annotations such as:

* `@RestController` on `CategoryController`
* `@Service` on `CategoryServiceImpl`

4. Spring creates `CategoryServiceImpl`. Since there is no constructor written in this class, Java provides a default no-argument constructor automatically.

5. Spring creates `CategoryController` by calling its constructor:

```java
public CategoryController(CategoryService categoryService) {
    this.categoryService = categoryService;
}
```

Spring sees that `CategoryController` needs a `CategoryService`, so it injects the available implementation, `CategoryServiceImpl`.

Conceptually, Spring is doing something similar to:

```java
CategoryService service = new CategoryServiceImpl();
CategoryController controller = new CategoryController(service);
```

### GET categories API flow

API:

```http
GET /api/public/categories
```

Controller class and method:

```java
@RequestMapping("/api")
public class CategoryController {

    @GetMapping("/public/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return new ResponseEntity<>(categoryService.getCategories(), HttpStatus.OK);
    }
}
```

Flow:

```text
Client calls GET /api/public/categories
        ↓
Class-level @RequestMapping("/api") matches /api
        ↓
Spring matches the request with @GetMapping
        ↓
CategoryController.getCategories() is called
        ↓
categoryService.getCategories() is called
        ↓
CategoryServiceImpl.getCategories() returns the categories list
        ↓
ResponseEntity tells Spring to return HTTP 200 OK with the category list
        ↓
Spring converts the List<Category> into JSON
        ↓
JSON response is sent to the client
```

If the list is empty, the response is:

```json
[]
```

If categories exist, the response looks like:

```json
[
  {
    "categoryId": 1,
    "categoryName": "Electronics"
  }
]
```

### POST category API flow

API:

```http
POST /api/public/categories
Content-Type: application/json
```

Request body:

```json
{
  "categoryName": "Electronics"
}
```

Controller class and method:

```java
@RequestMapping("/api")
public class CategoryController {

    @PostMapping("/public/categories")
    public ResponseEntity<String> createCategory(@RequestBody Category category) {
        categoryService.createCategory(category);
        return new ResponseEntity<>("Category created", HttpStatus.CREATED);
    }
}
```

Flow:

```text
Client calls POST /api/public/categories with JSON body
        ↓
Class-level @RequestMapping("/api") matches /api
        ↓
Spring matches the request with @PostMapping
        ↓
@RequestBody tells Spring to convert JSON into a Category object
        ↓
CategoryController.createCategory(category) is called
        ↓
categoryService.createCategory(category) is called
        ↓
CategoryServiceImpl.createCategory() adds the category to the list
        ↓
Controller returns ResponseEntity with HTTP 201 CREATED and body "Category created"
        ↓
Text response is sent to the client
```

Service method:

```java
public void createCategory(Category category) {
    categories.add(category);
}
```

### GET category by id API flow

API:

```http
GET /api/public/categories/{categoryId}
```

Example:

```http
GET /api/public/categories/1
```

Controller method:

```java
@GetMapping("/public/categories/{categoryId}")
public ResponseEntity<?> getCategoryById(@PathVariable long categoryId) {
    Category category = categoryService.getCategoryById(categoryId);

    if (category == null) {
        return new ResponseEntity<>("Category not found", HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(category, HttpStatus.OK);
}
```

Flow:

```text
Client calls GET /api/public/categories/1
        ↓
Spring reads 1 from the URL into categoryId using @PathVariable
        ↓
CategoryController.getCategoryById(categoryId) is called
        ↓
categoryService.getCategoryById(categoryId) searches the list
        ↓
If found, ResponseEntity returns HTTP 200 OK with the category
        ↓
If not found, ResponseEntity returns HTTP 404 NOT FOUND with "Category not found"
```

### PUT category update API flow

API:

```http
PUT /api/public/categories/{categoryId}
Content-Type: application/json
```

Example:

```http
PUT /api/public/categories/1
```

Request body:

```json
{
  "categoryName": "Mobiles"
}
```

For update, `categoryId` is not needed in the body. The id comes from the URL:

```text
/api/public/categories/1
```

So Spring reads `1` using `@PathVariable long categoryId`.

Controller method:

```java
@PutMapping("/public/categories/{categoryId}")
public ResponseEntity<String> updateCategory(@PathVariable long categoryId, @RequestBody Category category) {
    String status = categoryService.updateCategory(categoryId, category);

    if (status.equals("Category not found")) {
        return new ResponseEntity<>(status, HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(status, HttpStatus.OK);
}
```

Flow:

```text
Client calls PUT /api/public/categories/1 with JSON body
        ↓
Spring reads 1 from the URL into categoryId
        ↓
@RequestBody converts JSON into a Category object
        ↓
CategoryController.updateCategory(categoryId, category) is called
        ↓
categoryService.updateCategory(categoryId, category) searches and updates the category
        ↓
If found, ResponseEntity returns HTTP 200 OK with update message
        ↓
If not found, ResponseEntity returns HTTP 404 NOT FOUND with "Category not found"
```

### DELETE category API flow

API:

```http
DELETE /api/public/categories/{categoryId}
```

Example:

```http
DELETE /api/public/categories/1
```

Controller method:

```java
@DeleteMapping("/public/categories/{categoryId}")
public ResponseEntity<String> deleteCategory(@PathVariable long categoryId) {
    String status = categoryService.deleteCategory(categoryId);

    if (status.equals("Category not found")) {
        return new ResponseEntity<>(status, HttpStatus.NOT_FOUND);
    }

    return new ResponseEntity<>(status, HttpStatus.OK);
}
```

Flow:

```text
Client calls DELETE /api/public/categories/1
        ↓
Spring reads 1 from the URL into categoryId
        ↓
CategoryController.deleteCategory(categoryId) is called
        ↓
categoryService.deleteCategory(categoryId) removes the category from the list
        ↓
If found, ResponseEntity returns HTTP 200 OK with delete message
        ↓
If not found, ResponseEntity returns HTTP 404 NOT FOUND with "Category not found"
```

### Complete chain

```text
HTTP Request
   ↓
Spring DispatcherServlet
   ↓
CategoryController
   ↓
CategoryService interface
   ↓
CategoryServiceImpl
   ↓
ArrayList<Category>
   ↓
Return data
   ↓
Spring converts Java object to JSON or text
   ↓
HTTP Response
```

Note: for request body conversion, model classes commonly include a no-argument constructor. `Category` has an empty constructor so Spring/Jackson can create the object first and then set values from JSON.

`categoryId` uses `Long` instead of primitive `long`, so request bodies can omit `categoryId` or send it as `null`. This is useful for POST and PUT requests where the server or URL decides the id.

## ResponseEntity Notes

`ResponseEntity` is used when the controller should control both:

* The response body
* The HTTP status code

Without `ResponseEntity`, Spring only returns the Java value and usually uses HTTP 200 OK by default.

Example without `ResponseEntity`:

```java
public String createCategory() {
    return "Category created";
}
```

Example with `ResponseEntity` constructor notation:

```java
public ResponseEntity<String> createCategory() {
    return new ResponseEntity<>("Category created", HttpStatus.CREATED);
}
```

This means:

```text
Body: Category created
HTTP Status: 201 CREATED
```

### Common ResponseEntity notations

Constructor notation used in this project:

```java
return new ResponseEntity<>(category, HttpStatus.OK);
```

Custom status with body:

```java
return new ResponseEntity<>("Category not found", HttpStatus.NOT_FOUND);
```

The order is:

```java
new ResponseEntity<>(body, httpStatus)
```

No response body:

```java
return new ResponseEntity<>(HttpStatus.NO_CONTENT);
```

Bad request:

```java
return new ResponseEntity<>("Invalid category data", HttpStatus.BAD_REQUEST);
```

Generic response type when the method may return different body types:

```java
public ResponseEntity<?> getCategoryById(@PathVariable long categoryId)
```

Example: this method can return either a `Category` object or a `"Category not found"` string.

Specific response type when the body is always the same type:

```java
public ResponseEntity<List<Category>> getCategories()
public ResponseEntity<String> deleteCategory(@PathVariable long categoryId)
```

### Famous HTTP status codes

```text
200 OK                  Request succeeded
201 CREATED             New resource created successfully
204 NO CONTENT          Request succeeded, but there is no body to return
400 BAD REQUEST         Client sent invalid data
401 UNAUTHORIZED        User is not authenticated
403 FORBIDDEN           User is authenticated but not allowed
404 NOT FOUND           Requested resource does not exist
409 CONFLICT            Request conflicts with current data
500 INTERNAL SERVER ERROR Server-side error
```

In this project:

```text
GET all categories      200 OK
GET category by id      200 OK if found, 404 NOT FOUND if missing
POST category           201 CREATED
PUT category            200 OK if updated, 404 NOT FOUND if missing
DELETE category         200 OK if deleted, 404 NOT FOUND if missing
```

## RequestMapping Notes

`@RequestMapping` can be used at the class level to define a common path prefix for all endpoints inside that controller.

Current controller:

```java
@RestController
@RequestMapping("/api")
public class CategoryController {
}
```

Because `/api` is already at the top, each method only needs to define the remaining path.

Example:

```java
@GetMapping("/public/categories")
```

Final API path becomes:

```text
/api/public/categories
```

The final path is formed like this:

```text
Class-level path + Method-level path
/api             + /public/categories
= /api/public/categories
```

This keeps the controller cleaner because `/api` does not need to be repeated on every endpoint.

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/4.0.6/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/4.0.6/maven-plugin/build-image.html)
* [Spring Web](https://docs.spring.io/spring-boot/4.0.6/reference/web/servlet.html)

### Guides
The following guides illustrate how to use some features concretely:

* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.
