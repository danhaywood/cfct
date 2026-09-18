package com.danhaywood.cfct.spi;

import com.danhaywood.cfct.model.CommandExecutionTimingObservation;
import com.danhaywood.cfct.model.ExecutionTimingScope;
import com.danhaywood.cfct.model.ExecutionTimingSide;

import java.sql.Connection;
import java.util.Collection;
import java.util.List;

public interface CommandExecutionTimingReader {

    List<CommandExecutionTimingObservation> read(
            Connection connection,
            Collection<String> interactionIds,
            ExecutionTimingSide side,
            ExecutionTimingScope scope);
}
