using UnityEditor;
using UnityEngine;

public class WebGLBuilder
{
    public static void Build()
    {
        string[] scenes = {"Assets/Scenes/MainScene.unity"};
        string buildPath = "Builds/WebGL"; // Output path for the build

        // Ensure the build directory exists
        if (!System.IO.Directory.Exists(buildPath))
        {
            System.IO.Directory.CreateDirectory(buildPath);
        }
        
        // Perform the build
        BuildPipeline.BuildPlayer(scenes, buildPath, BuildTarget.WebGL, BuildOptions.None);

        Debug.Log("WebGL build completed successfully.");
    }
}
