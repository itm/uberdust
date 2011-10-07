// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: command.proto

package eu.uberdust.controller.protobuf;

public final class CommandProtocol {
  private CommandProtocol() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
  }
  public static final class Command extends
      com.google.protobuf.GeneratedMessage {
    // Use Command.newBuilder() to construct.
    private Command() {
      initFields();
    }
    private Command(boolean noInit) {}
    
    private static final Command defaultInstance;
    public static Command getDefaultInstance() {
      return defaultInstance;
    }
    
    public Command getDefaultInstanceForType() {
      return defaultInstance;
    }
    
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return eu.uberdust.controller.protobuf.CommandProtocol.internal_static_controller_Command_descriptor;
    }
    
    protected com.google.protobuf.GeneratedMessage.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return eu.uberdust.controller.protobuf.CommandProtocol.internal_static_controller_Command_fieldAccessorTable;
    }
    
    // optional string destination = 1;
    public static final int DESTINATION_FIELD_NUMBER = 1;
    private boolean hasDestination;
    private java.lang.String destination_ = "";
    public boolean hasDestination() { return hasDestination; }
    public java.lang.String getDestination() { return destination_; }
    
    // optional string payload = 2;
    public static final int PAYLOAD_FIELD_NUMBER = 2;
    private boolean hasPayload;
    private java.lang.String payload_ = "";
    public boolean hasPayload() { return hasPayload; }
    public java.lang.String getPayload() { return payload_; }
    
    private void initFields() {
    }
    public final boolean isInitialized() {
      return true;
    }
    
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      getSerializedSize();
      if (hasDestination()) {
        output.writeString(1, getDestination());
      }
      if (hasPayload()) {
        output.writeString(2, getPayload());
      }
      getUnknownFields().writeTo(output);
    }
    
    private int memoizedSerializedSize = -1;
    public int getSerializedSize() {
      int size = memoizedSerializedSize;
      if (size != -1) return size;
    
      size = 0;
      if (hasDestination()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(1, getDestination());
      }
      if (hasPayload()) {
        size += com.google.protobuf.CodedOutputStream
          .computeStringSize(2, getPayload());
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSerializedSize = size;
      return size;
    }
    
    public static eu.uberdust.controller.protobuf.CommandProtocol.Command parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static eu.uberdust.controller.protobuf.CommandProtocol.Command parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static eu.uberdust.controller.protobuf.CommandProtocol.Command parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data).buildParsed();
    }
    public static eu.uberdust.controller.protobuf.CommandProtocol.Command parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return newBuilder().mergeFrom(data, extensionRegistry)
               .buildParsed();
    }
    public static eu.uberdust.controller.protobuf.CommandProtocol.Command parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static eu.uberdust.controller.protobuf.CommandProtocol.Command parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    public static eu.uberdust.controller.protobuf.CommandProtocol.Command parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static eu.uberdust.controller.protobuf.CommandProtocol.Command parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Builder builder = newBuilder();
      if (builder.mergeDelimitedFrom(input, extensionRegistry)) {
        return builder.buildParsed();
      } else {
        return null;
      }
    }
    public static eu.uberdust.controller.protobuf.CommandProtocol.Command parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input).buildParsed();
    }
    public static eu.uberdust.controller.protobuf.CommandProtocol.Command parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return newBuilder().mergeFrom(input, extensionRegistry)
               .buildParsed();
    }
    
    public static Builder newBuilder() { return Builder.create(); }
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder(eu.uberdust.controller.protobuf.CommandProtocol.Command prototype) {
      return newBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() { return newBuilder(this); }
    
    public static final class Builder extends
        com.google.protobuf.GeneratedMessage.Builder<Builder> {
      private eu.uberdust.controller.protobuf.CommandProtocol.Command result;
      
      // Construct using eu.uberdust.controller.protobuf.CommandProtocol.Command.newBuilder()
      private Builder() {}
      
      private static Builder create() {
        Builder builder = new Builder();
        builder.result = new eu.uberdust.controller.protobuf.CommandProtocol.Command();
        return builder;
      }
      
      protected eu.uberdust.controller.protobuf.CommandProtocol.Command internalGetResult() {
        return result;
      }
      
      public Builder clear() {
        if (result == null) {
          throw new IllegalStateException(
            "Cannot call clear() after build().");
        }
        result = new eu.uberdust.controller.protobuf.CommandProtocol.Command();
        return this;
      }
      
      public Builder clone() {
        return create().mergeFrom(result);
      }
      
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return eu.uberdust.controller.protobuf.CommandProtocol.Command.getDescriptor();
      }
      
      public eu.uberdust.controller.protobuf.CommandProtocol.Command getDefaultInstanceForType() {
        return eu.uberdust.controller.protobuf.CommandProtocol.Command.getDefaultInstance();
      }
      
      public boolean isInitialized() {
        return result.isInitialized();
      }
      public eu.uberdust.controller.protobuf.CommandProtocol.Command build() {
        if (result != null && !isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return buildPartial();
      }
      
      private eu.uberdust.controller.protobuf.CommandProtocol.Command buildParsed()
          throws com.google.protobuf.InvalidProtocolBufferException {
        if (!isInitialized()) {
          throw newUninitializedMessageException(
            result).asInvalidProtocolBufferException();
        }
        return buildPartial();
      }
      
      public eu.uberdust.controller.protobuf.CommandProtocol.Command buildPartial() {
        if (result == null) {
          throw new IllegalStateException(
            "build() has already been called on this Builder.");
        }
        eu.uberdust.controller.protobuf.CommandProtocol.Command returnMe = result;
        result = null;
        return returnMe;
      }
      
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof eu.uberdust.controller.protobuf.CommandProtocol.Command) {
          return mergeFrom((eu.uberdust.controller.protobuf.CommandProtocol.Command)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }
      
      public Builder mergeFrom(eu.uberdust.controller.protobuf.CommandProtocol.Command other) {
        if (other == eu.uberdust.controller.protobuf.CommandProtocol.Command.getDefaultInstance()) return this;
        if (other.hasDestination()) {
          setDestination(other.getDestination());
        }
        if (other.hasPayload()) {
          setPayload(other.getPayload());
        }
        this.mergeUnknownFields(other.getUnknownFields());
        return this;
      }
      
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder(
            this.getUnknownFields());
        while (true) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              this.setUnknownFields(unknownFields.build());
              return this;
            default: {
              if (!parseUnknownField(input, unknownFields,
                                     extensionRegistry, tag)) {
                this.setUnknownFields(unknownFields.build());
                return this;
              }
              break;
            }
            case 10: {
              setDestination(input.readString());
              break;
            }
            case 18: {
              setPayload(input.readString());
              break;
            }
          }
        }
      }
      
      
      // optional string destination = 1;
      public boolean hasDestination() {
        return result.hasDestination();
      }
      public java.lang.String getDestination() {
        return result.getDestination();
      }
      public Builder setDestination(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasDestination = true;
        result.destination_ = value;
        return this;
      }
      public Builder clearDestination() {
        result.hasDestination = false;
        result.destination_ = getDefaultInstance().getDestination();
        return this;
      }
      
      // optional string payload = 2;
      public boolean hasPayload() {
        return result.hasPayload();
      }
      public java.lang.String getPayload() {
        return result.getPayload();
      }
      public Builder setPayload(java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  result.hasPayload = true;
        result.payload_ = value;
        return this;
      }
      public Builder clearPayload() {
        result.hasPayload = false;
        result.payload_ = getDefaultInstance().getPayload();
        return this;
      }
      
      // @@protoc_insertion_point(builder_scope:controller.Command)
    }
    
    static {
      defaultInstance = new Command(true);
      eu.uberdust.controller.protobuf.CommandProtocol.internalForceInit();
      defaultInstance.initFields();
    }
    
    // @@protoc_insertion_point(class_scope:controller.Command)
  }
  
  private static com.google.protobuf.Descriptors.Descriptor
    internal_static_controller_Command_descriptor;
  private static
    com.google.protobuf.GeneratedMessage.FieldAccessorTable
      internal_static_controller_Command_fieldAccessorTable;
  
  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rcommand.proto\022\ncontroller\"/\n\007Command\022\023" +
      "\n\013destination\030\001 \001(\t\022\017\n\007payload\030\002 \001(\tB2\n\037" +
      "eu.uberdust.controller.protobufB\017Command" +
      "Protocol"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
      new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
        public com.google.protobuf.ExtensionRegistry assignDescriptors(
            com.google.protobuf.Descriptors.FileDescriptor root) {
          descriptor = root;
          internal_static_controller_Command_descriptor =
            getDescriptor().getMessageTypes().get(0);
          internal_static_controller_Command_fieldAccessorTable = new
            com.google.protobuf.GeneratedMessage.FieldAccessorTable(
              internal_static_controller_Command_descriptor,
              new java.lang.String[] { "Destination", "Payload", },
              eu.uberdust.controller.protobuf.CommandProtocol.Command.class,
              eu.uberdust.controller.protobuf.CommandProtocol.Command.Builder.class);
          return null;
        }
      };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
  }
  
  public static void internalForceInit() {}
  
  // @@protoc_insertion_point(outer_class_scope)
}
