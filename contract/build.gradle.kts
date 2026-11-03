plugins {
    `java-library`
    id("com.google.protobuf") version "0.10.0"
}

repositories {
    mavenCentral()
}

val grpcVersion = "1.76.0"
val protobufVersion = "4.32.1"

dependencies {
    api("io.grpc:grpc-protobuf:$grpcVersion")
    api("io.grpc:grpc-stub:$grpcVersion")
    api("com.google.protobuf:protobuf-java:$protobufVersion")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53")
}

protobuf {
    protoc { artifact = "com.google.protobuf:protoc:$protobufVersion" }
    plugins {
        create("grpc") { artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion" }
    }
    generateProtoTasks {
        all().forEach { task -> task.plugins { create("grpc") } }
    }
}
