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

Controller method:

```java
@GetMapping("/api/public/categories")
public List<Category> getCategories() {
    return categoryService.getCategories();
}
```

Flow:

```text
Client calls GET /api/public/categories
        ↓
Spring matches the request with @GetMapping
        ↓
CategoryController.getCategories() is called
        ↓
categoryService.getCategories() is called
        ↓
CategoryServiceImpl.getCategories() returns the categories list
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
  "categoryId": 1,
  "categoryName": "Electronics"
}
```

Controller method:

```java
@PostMapping("/api/public/categories")
public String createCategory(@RequestBody Category category) {
    categoryService.createCategory(category);
    return "Category created";
}
```

Flow:

```text
Client calls POST /api/public/categories with JSON body
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
Controller returns "Category created"
        ↓
Text response is sent to the client
```

Service method:

```java
public void createCategory(Category category) {
    categories.add(category);
}
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

Note: for request body conversion, model classes commonly include a no-argument constructor. `Category` currently has only a parameterized constructor, so adding an empty constructor may be needed if POST request conversion fails.

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
