package com.danhaywood.cfct.spi;

import java.sql.Connection;
import java.util.Collection;
import java.util.SortedSet;

public interface CommandAuditTouchedTableResolver {

    SortedSet<String> resolveTouchedQualifiedTableNames(Connection connection, Collection<String> interactionIds);
}
