import { useState } from "react";

export default function usePost(url) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(false);

  const execute = async (formData) => {
    setLoading(true);
    setError(null);
    setSuccess(false);

const TEST_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJyb2JtYWNoIiwiaWF0IjoxNzcxNDQzMDk5LCJleHAiOjE3NzE0NDY2OTl9.Gs1WBkecQrIOqKtyBBEj20mStObXvnUeXCS4ILr6ULM"

    try {
      const response = await fetch(url, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${TEST_TOKEN}`
        },
        body: JSON.stringify(formData),
      });

      if (!response.ok) throw new Error(`Status: ${response.status}`);
      const text = await response.text();
      const json = text ? JSON.parse(text) : {};
      setSuccess(true);
      return json; // Return data in case you need it in the component
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return { execute, loading, error, success };
}