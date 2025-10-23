# 🧩 Exception Maker

**Exception Maker** is a compile-time code generation library that automatically creates custom exception classes and helper methods for handling exceptions in Spring-based applications.

---

## ⚙️ Overview

This library allows you to:

- **Generate exception classes** at **compile time** that automatically extend `RuntimeException`.
- **Generate methods** to handle or wrap exceptions declared in your annotated interfaces.
- Simplify exception handling logic in large-scale Spring applications.

> ⚠️ **Note:** The library currently supports **only Spring applications**.

---

## 🧱 How It Works

1. Annotate an **interface** with `@ExceptionMaker`.
2. Annotate the **method signatures** inside that interface with `@ExceptionRunner`.
3. During compilation, the annotation processor generates:
   - One exception class for each element in the `classesName` list of `@ExceptionMaker`.
   - Optional helper methods for handling the specified exceptions.

### Example

```java
@ExceptionMaker(classesName = {"UserNotFoundException", "InvalidRequestException"})
public interface UserExceptionHandler {

    @ExceptionRunner(exceptionClass = "UserNotFoundException")
    void handleUserNotFound(String message);

    @ExceptionRunner(exceptionClass = "InvalidRequestException")
    void handleInvalidRequest(String message, Object[] params);
}

