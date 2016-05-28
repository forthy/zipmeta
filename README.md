# ZipMetadataService

Enables you to fetch metadata from a remote zip file without
downloading the entire file. This is useful if you don't know if the
zip archive has the file you're looking for, and you don't want to
download the entire file.

## Example

```
// List names of all files in remote zip file
service.getMetadata("http://www.colorado.edu/conflict/peace/download/peace.zip").map {
    metadata =>
        metadata.directoryRecords.map(record => println(record.fileName))
}
```