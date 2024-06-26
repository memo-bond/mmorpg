using System.Collections;
using Creator_Kit___RPG.Scripts.Connection;
using Creator_Kit___RPG.Scripts.Gameplay;
using NativeWebSocket;
using UnityEngine;
using UnityEngine.U2D;
using Random = UnityEngine.Random;

namespace RPGM.Gameplay
{
    /// <summary>
    /// A simple controller for animating a 4 directional sprite using Physics.
    /// </summary>
    public class CharacterController2D : MonoBehaviour
    {
        [SerializeField] private bool online = false;

        [SerializeField] private ConnectionManager client;

        public float speed = 1;
        public float acceleration = 2;
        public Vector3 nextMoveCommand;
        public Animator animator;
        public bool flipX = false;

        new Rigidbody2D rigidbody2D;
        SpriteRenderer spriteRenderer;
        PixelPerfectCamera pixelPerfectCamera;

        private Vector3 lastPosition;
        private float positionUpdateThreshold = 0.2f;

        enum State
        {
            Idle,
            Moving
        }

        State state = State.Idle;
        Vector3 start, end;
        Vector2 currentVelocity;
        float startTime;
        float distance;
        float velocity;

        private bool main;
        private uint id;
        private string name;
        private Vector3 position;

        public bool Main
        {
            get { return main; }
            set { main = value; }
        }

        public uint Id
        {
            get { return id; }
            set { id = value; }
        }

        public string Name
        {
            get { return name; }
            set { name = value; }
        }

        public Vector3 Position
        {
            get { return position; }
            set { position = value; }
        }

        private void Start()
        {
            main = true;
            lastPosition = transform.position;

            if (online && main)
            {
                Debug.Log("Joining To Server");
                id = (uint)Random.Range(1, 99);
                PlayerMessage msg = new()
                {
                    Join = new()
                    {
                        Id = id,
                        Name = "Lucas",
                        X = transform.position.x,
                        Y = transform.position.y
                    }
                };
                StartCoroutine(SendJoinMsg(msg));
            }
        }

        private IEnumerator SendJoinMsg(PlayerMessage msg)
        {
            Debug.Log("Start send join msg");

            float timeout = 10f; // Timeout after 10 seconds
            float elapsedTime = 0f;

            while (client.State() != WebSocketState.Open && elapsedTime < timeout)
            {
                Debug.Log("Check State " + client.State());
                yield return new WaitForSeconds(0.3f);
                elapsedTime += 0.3f;
            }

            if (client.State() == WebSocketState.Open)
            {
                Debug.Log("Send Join Msg To Server " + msg);
                _ = client.Send(msg);
            }
            else
            {
                Debug.LogError("WebSocket connection failed to open within timeout period.");
            }
        }


        private void IdleState()
        {
            if (nextMoveCommand != Vector3.zero)
            {
                start = transform.position;
                end = start + nextMoveCommand;
                distance = (end - start).magnitude;
                velocity = 0;
                UpdateAnimator(nextMoveCommand);
                nextMoveCommand = Vector3.zero;
                state = State.Moving;
            }
        }

        private void MoveState()
        {
            velocity = Mathf.Clamp01(velocity + Time.deltaTime * acceleration);
            UpdateAnimator(nextMoveCommand);
            rigidbody2D.velocity = Vector2.SmoothDamp(rigidbody2D.velocity,
                nextMoveCommand * speed, ref currentVelocity, acceleration, speed);
            spriteRenderer.flipX = rigidbody2D.velocity.x >= 0 ? true : false;

            if (!online) return; // guard
            if (!(Vector3.Distance(transform.position, lastPosition) > positionUpdateThreshold)) return; // guard
            if (!main)
            {
                nextMoveCommand = Vector3.zero;
                return;
            }
            Debug.Log($"nextMoveCommand {nextMoveCommand}");
            SendMoveMsg();
            lastPosition = transform.position;
        }

        private void SendMoveMsg()
        {
            PlayerMessage msg = new()
            {
                Move = new()
                {
                    Id = id,
                    X = transform.position.x,
                    Y = CoordinateConverter.UnityToAoiY(transform.position.y)
                }
            };

            Debug.Log($"Player Move {msg}");
            _ = client.Send(msg);
        }

        private void UpdateAnimator(Vector3 direction)
        {
            if (animator)
            {
                animator.SetInteger("WalkX", direction.x < 0 ? -1 : direction.x > 0 ? 1 : 0);
                animator.SetInteger("WalkY", direction.y < 0 ? 1 : direction.y > 0 ? -1 : 0);
            }
        }

        private void Update()
        {
            switch (state)
            {
                case State.Idle:
                    IdleState();
                    break;
                case State.Moving:
                    MoveState();
                    break;
            }
        }

        private void LateUpdate()
        {
            if (pixelPerfectCamera)
            {
                transform.position = pixelPerfectCamera.RoundToPixel(transform.position);
            }
        }

        private void Awake()
        {
            rigidbody2D = GetComponent<Rigidbody2D>();
            spriteRenderer = GetComponent<SpriteRenderer>();
            pixelPerfectCamera = FindObjectOfType<PixelPerfectCamera>();
        }

        private async void OnApplicationQuit()
        {
            PlayerMessage msg = new()
            {
                Quit = new()
                {
                    Id = id
                }
            };
            await client.Send(msg);
        }
    }
}