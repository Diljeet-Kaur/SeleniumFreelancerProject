# Multi-Files-Parser
Multi-File Parser is having ability to parse XML, JSON, XLS and CSV. It is written on top of various industry standard parsers.
The API is **simple to use**, independent in nature and is written to **perform and scale**.

- It is written using Strategy so new strategies i.e. Parsers for new formats can be added adhering Single Responsibility.
- Ability to read files from a directory or File[].
- Supports paralellism i.e. processes files in parallel using Java 7 Fork/Join.
- Easy data validation support using JSR-303 Bean Validation annotations.
- Uses Notification Pattern ([bliki](http://martinfowler.com/eaaDev/Notification.html)) for validation errors.
- Spring Support

##Usage
Simple usage with a single line of code.
