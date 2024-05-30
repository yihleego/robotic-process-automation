export default {
  apps: [
    {
      id: "wechat",
      name: "WeChat",
      tasks: [
        {
          key: "login",
          code: "login",
          name: "Login",
          remark: "",
          priority: 0,
          definitions: [],
        },
        {
          key: "logout",
          code: "logout",
          name: "Logout",
          remark: "",
          priority: 1,
          definitions: [],
        },
        {
          key: "sendPrivateMessages",
          code: "send_private_messages",
          name: "Send Private Messages",
          remark: "",
          priority: 100,
          definitions: [
            {
              key: "target",
              value: null,
              name: "Friend",
              type: "string", // number, string, radio, checkbox, file, multiple
              required: true
            },
            {
              key: "messages",
              value: null,
              name: "Messages",
              type: "multiple",
              required: true,
              children: [
                {
                  key: "type",
                  value: null,
                  name: "Message Type",
                  type: "radio",
                  required: true,
                  options: [
                    {
                      name: "Text",
                      value: "text"
                    },
                    {
                      name: "Image",
                      value: "image"
                    },
                    {
                      name: "Video",
                      value: "video"
                    },
                    {
                      name: "File",
                      value: "file"
                    },
                  ],
                },
                {
                  key: "content",
                  value: null,
                  name: "Content",
                  type: "string",
                  required: true,
                }
              ]
            }
          ]
        }
      ]
    }
  ]
}