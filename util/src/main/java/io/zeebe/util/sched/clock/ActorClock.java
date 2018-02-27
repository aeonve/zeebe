/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
 *
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
package io.zeebe.util.sched.clock;

import io.zeebe.util.sched.ActorTaskRunner;

public interface ActorClock
{
    void update();

    long getTimeMillis();

    long getNanosSinceLastMillisecond();

    long getNanoTime();

    static ActorClock current()
    {
        final ActorTaskRunner current = ActorTaskRunner.current();
        if (current == null)
        {
            throw new UnsupportedOperationException("ActorClock.current() can only be called from actor thread.");
        }

        return current.getClock();
    }

    static long currentTimeMillis()
    {
        return current().getTimeMillis();
    }
}