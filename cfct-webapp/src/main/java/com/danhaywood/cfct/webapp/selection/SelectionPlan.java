package com.danhaywood.cfct.webapp.selection;

import com.danhaywood.cfct.model.TableRef;

import java.util.List;

/**
 * Strategy abstraction for resolving webapp comparison targets.
 */
public interface SelectionPlan {

    List<TableRef> resolveTables();
}
