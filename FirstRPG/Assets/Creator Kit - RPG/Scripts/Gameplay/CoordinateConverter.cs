using UnityEngine;

namespace Creator_Kit___RPG.Scripts.Gameplay
{
    public class CoordinateConverter
    {
        private static int gridHeight = 1000;
        
        public static int UnityToAoiY(float unityY)
        {
            return gridHeight - Mathf.RoundToInt(unityY);
        }
        
        public static float AoiToUnityY(int aoiY)
        {
            return gridHeight - aoiY;
        }
    }
}