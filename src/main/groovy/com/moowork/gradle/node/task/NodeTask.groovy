package com.moowork.gradle.node.task

import com.moowork.gradle.node.NodePlugin
import com.moowork.gradle.node.exec.NodeExecRunner
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.TaskAction
import org.gradle.process.ExecResult

import static org.gradle.api.tasks.PathSensitivity.RELATIVE

class NodeTask
    extends DefaultTask
{
    protected NodeExecRunner runner

    private File script

    private List<?> args = []

    private Iterable<?> options = []

    private ExecResult result

    NodeTask()
    {
        this.group = NodePlugin.NODE_GROUP
        this.runner = new NodeExecRunner( this.project )
        dependsOn( SetupTask.NAME )
    }

    void setScript( final File value )
    {
        this.script = value
    }

    void setArgs( final Iterable<?> value )
    {
        this.args = value.asList()
    }

    void addArgs( final Object... args )
    {
        this.args.addAll( args )
    }

    void setOptions( final Iterable<?> value )
    {
        this.options = value
    }

    void setEnvironment( final Map<String, ?> value )
    {
        this.runner.environment << value
    }

    void setWorkingDir( final File workingDir )
    {
        this.runner.workingDir = workingDir
    }

    void setIgnoreExitValue( final boolean value )
    {
        this.runner.ignoreExitValue = value
    }

    void setExecOverrides( final Closure closure )
    {
        this.runner.execOverrides = closure
    }

    @Internal
    ExecResult getResult()
    {
        return this.result
    }

    @InputFiles
    @PathSensitive(RELATIVE)
    File getScript()
    {
        return this.script
    }

    @Input
    List<?> getArgs()
    {
        return this.args
    }

    @Input
    Iterable<?> getOptions()
    {
        return this.options
    }

    @Nested
    NodeExecRunner getRunner()
    {
        return this.runner
    }

    @TaskAction
    void exec()
    {
        if ( this.script == null )
        {
            throw new IllegalStateException( 'Required script property is not set.' )
        }

        def execArgs = []
        execArgs.addAll( this.options as List )
        execArgs.add( this.script.absolutePath )
        execArgs.addAll( this.args as List )

        this.runner.arguments = execArgs
        this.result = this.runner.execute()
    }
}
