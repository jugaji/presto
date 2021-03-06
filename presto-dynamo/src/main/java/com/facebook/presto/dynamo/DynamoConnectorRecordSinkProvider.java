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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import com.facebook.presto.spi.ConnectorInsertTableHandle;
import com.facebook.presto.spi.ConnectorOutputTableHandle;
import com.facebook.presto.spi.ConnectorRecordSinkProvider;
import com.facebook.presto.spi.RecordSink;

public class DynamoConnectorRecordSinkProvider
        implements ConnectorRecordSinkProvider
{
    private final DynamoSession dynamoSession;

    @Inject
    public DynamoConnectorRecordSinkProvider(DynamoSession dynamoSession)
    {
        this.dynamoSession = checkNotNull(dynamoSession, "dynamoSession is null");
    }

    @Override
    public RecordSink getRecordSink(ConnectorOutputTableHandle tableHandle)
    {
        checkNotNull(tableHandle, "tableHandle is null");
        checkArgument(tableHandle instanceof DynamoOutputTableHandle, "tableHandle is not an instance of DynamoOutputTableHandle");
        DynamoOutputTableHandle handle = (DynamoOutputTableHandle) tableHandle;

        return new DynamoRecordSink(handle, dynamoSession);
    }

    @Override
    public RecordSink getRecordSink(ConnectorInsertTableHandle tableHandle)
    {
        throw new UnsupportedOperationException();
    }
}
