package com.danhaywood.cfct.model;

public record AuditTrailEntryDescriptor(
        String interactionId,
        int sequence,
        String target,
        String memberIdentifier) {
}
