# ZipMetadataService

Enables you to fetch metadata from a remote zip file without
downloading the entire file. This is useful if you don't know if the
zip archive has the file you're looking for, and you don't want to
download the entire file.

## SBT Coordinates

```
libraryDependencies += "com.snacktrace" % "zipmeta_2.11" % "1.1.0"
```

## Example

```
// Construct the service
val client = new ZipFileClientApache(new DefaultHttpClient())
val service = new ZipMetadataServiceImpl(client)

// List names of all files in remote zip file
service.getMetadata("http://www.colorado.edu/conflict/peace/download/peace.zip").map {
    metadata =>
        metadata.directoryRecords.map(record => println(record.fileName))
}

// Get a file that matches the given pattern
service.getFile("http://www.colorado.edu/conflict/peace/download/peace.zip", "^ab_cit.*$").map {
    file =>
        println(file.content)
}
```
