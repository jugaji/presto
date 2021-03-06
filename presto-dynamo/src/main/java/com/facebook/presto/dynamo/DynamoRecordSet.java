/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.dynamo;

import static com.facebook.presto.dynamo.DynamoColumnHandle.dynamoFullTypeGetter;
import static com.facebook.presto.dynamo.DynamoColumnHandle.nativeTypeGetter;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.transform;

import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.facebook.presto.spi.RecordCursor;
import com.facebook.presto.spi.RecordSet;
import com.facebook.presto.spi.type.Type;

public class DynamoRecordSet
        implements RecordSet
{
    private final AmazonDynamoDB dynamoClient;
    private final String tableName;
    private final List<String> columnNames;
    private final List<FullDynamoType> dynamoTypes;
    private final List<Type> columnTypes;
    private final int fetchSize;

    public DynamoRecordSet(AmazonDynamoDB dynamoClient, String tableName, List<DynamoColumnHandle> dynamoColumns, int fetchSize)
    {
        this.dynamoClient = checkNotNull(dynamoClient, "dynamoClient is null");
        this.tableName = checkNotNull(tableName, "tableName is null");
        checkNotNull(dynamoColumns, "dynamoColumns is null");
        this.dynamoTypes = transform(dynamoColumns, dynamoFullTypeGetter());
        this.columnTypes = transform(dynamoColumns, nativeTypeGetter());
        this.columnNames = new ArrayList<String>();
        for (DynamoColumnHandle entry : dynamoColumns) {
            this.columnNames.add(entry.getName());
        }
        this.fetchSize = fetchSize;
    }

    @Override
    public List<Type> getColumnTypes()
    {
        return columnTypes;
    }

    @Override
    public RecordCursor cursor()
    {
        return new DynamoRecordCursor(dynamoClient, tableName, dynamoTypes, columnNames, fetchSize);
    }
}
