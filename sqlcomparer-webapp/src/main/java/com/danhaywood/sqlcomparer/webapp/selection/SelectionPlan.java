package com.danhaywood.sqlcomparer.webapp.selection;

import com.danhaywood.sqlcomparer.model.TableRef;

import java.util.List;

/**
 * Strategy abstraction for resolving webapp comparison targets.
 */
public interface SelectionPlan {

    List<TableRef> resolveTables();
}
