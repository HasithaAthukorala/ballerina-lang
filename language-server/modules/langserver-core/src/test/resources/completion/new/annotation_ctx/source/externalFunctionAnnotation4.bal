import ballerina/module1;

function testFunction() = @module1:s external;

public type AnnotationType record {
    string foo;
    int bar?;
};

public const annotation AnnotationType sourceExternalAnnotation1 on source external;

public const annotation sourceExternalAnnotation2 on source external;
