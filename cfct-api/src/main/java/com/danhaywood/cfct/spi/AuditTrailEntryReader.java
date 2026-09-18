package com.danhaywood.cfct.spi;

import com.danhaywood.cfct.model.AuditTrailEntryDescriptor;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

public interface AuditTrailEntryReader {

    List<AuditTrailEntryDescriptor> read(Connection connection, Collection<String> interactionIds);
}
